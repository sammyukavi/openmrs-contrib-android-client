/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.api.retrofit;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.activeandroid.query.Select;

import org.jdeferred.DoneCallback;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.multiple.MultipleResults;
import org.openmrs.mobile.api.EncounterService;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.promise.SimpleDeferredObject;
import org.openmrs.mobile.api.promise.SimplePromise;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.models.EncounterCreate;
import org.openmrs.mobile.models.IdGenPatientIdentifiers;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientIdentifier;
import org.openmrs.mobile.models.PatientIdentifierType;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PatientApi extends RetrofitApi {

	public static final String FULL_REPRESENTATION = "full";
	private OpenMRSLogger logger = new OpenMRSLogger();
	private PatientDAO patientDao = new PatientDAO();

	/**
	 * Sync Patient
	 */
	public SimplePromise<Patient> syncPatient(final Patient patient) {
		return syncPatient(patient, null);
	}

	public SimplePromise<Patient> syncPatient(final Patient patient,
			@Nullable final DefaultResponseCallbackListener callbackListener) {
		final SimpleDeferredObject<Patient> deferred = new SimpleDeferredObject<>();

		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance());
		Boolean syncState = prefs.getBoolean("sync", true);

		if (syncState) {
			AndroidDeferredManager dm = new AndroidDeferredManager();
			dm.when(new LocationApi().getLocationUuid(), getIdGenPatientIdentifier(), getPatientIdentifierTypeUuid())
					.done(new DoneCallback<MultipleResults>() {
						@Override
						public void onDone(final MultipleResults results) {
							final List<PatientIdentifier> identifiers = new ArrayList<>();

							final PatientIdentifier identifier = new PatientIdentifier();
							identifier.setLocation((Location)results.get(0).getResult());
							identifier.setIdentifier((String)results.get(1).getResult());
							identifier.setIdentifierType((PatientIdentifierType)results.get(2).getResult());
							identifiers.add(identifier);

							patient.setIdentifiers(identifiers);
							patient.setUuid(null);

							final RestApi apiService =
									RestServiceBuilder.createService(RestApi.class);
							Call<Patient> call = apiService.createPatient(patient);
							call.enqueue(new Callback<Patient>() {
								@Override
								public void onResponse(Call<Patient> call, Response<Patient> response) {
									if (response.isSuccessful()) {
										Patient newPatient = response.body();

										patient.setUuid(newPatient.getUuid());
										patient.getPerson().setUuid(newPatient.getUuid());
										/*if (patient.getPerson().getPhoto() != null)
											uploadPatientPhoto(patient);*/

										new PatientDAO().updatePatient(patient.getId(), patient);
										if (!patient.getEncounters().equals(""))
											addEncounters(patient);

										deferred.resolve(patient);

										if (callbackListener != null) {
											callbackListener.onResponse();
										}

									} else {
										ToastUtil.error(
												"Patient[" + patient.getId() + "] cannot be synced due to server error" +
														response.message());
										deferred.reject(new RuntimeException(
												"Patient cannot be synced due to server error: " +
														response.errorBody().toString()));
										if (callbackListener != null) {
											callbackListener.onErrorResponse(response.message());
										}
									}
								}

								@Override
								public void onFailure(Call<Patient> call, Throwable t) {
									ToastUtil.notify(
											"Patient[" + patient.getId() + "] cannot be synced due to request error: " +
													t.toString());
									deferred.reject(t);
									if (callbackListener != null) {
										callbackListener.onErrorResponse(t.getMessage());
									}
								}
							});
						}
					});
		} else {
			ToastUtil.notify("Sync is off. Patient Registration data is saved locally " +
					"and will sync when online mode is restored. ");
			if (callbackListener != null) {
				callbackListener.onResponse();
			}
		}

		return deferred.promise();
	}
	
	/*private void uploadPatientPhoto(final Patient patient) {
		RestApi apiService = RestServiceBuilder.createService(RestApi.class);
		PatientPhoto patientPhoto = new PatientPhoto();
		patientPhoto.setPhoto(patient.getPerson().getPhoto());
		patientPhoto.setPerson(patient.getPerson());
		Call<PatientPhoto> personPhotoCall =
				apiService.uploadPatientPhoto(patient.getUuid(), patientPhoto);
		personPhotoCall.enqueue(new Callback<PatientPhoto>() {
			@Override
			public void onResponse(Call<PatientPhoto> call, Response<PatientPhoto> response) {
				logger.i(response.message());
				if (!response.isSuccessful()) {
					ToastUtil.error("Patient photo cannot be synced due to server error: " + response.message());
				}
			}
			
			@Override
			public void onFailure(Call<PatientPhoto> call, Throwable t) {
				ToastUtil.notify("Patient photo cannot be synced due to error: " + t.toString());
			}
		});
	}*/

	/**
	 * Register Patient
	 */
	public SimplePromise<Patient> registerPatient(final Patient patient) {
		return registerPatient(patient, null);
	}

	public SimplePromise<Patient> registerPatient(final Patient patient,
			@Nullable final DefaultResponseCallbackListener callbackListener) {
		patientDao.savePatient(patient);
		if (callbackListener != null) {
			return syncPatient(patient, callbackListener);
		} else {
			return syncPatient(patient);
		}
	}

	/**
	 * Update Patient
	 */
	public void updatePatient(final Patient patient, @Nullable final DefaultResponseCallbackListener callbackListener) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance());
		Boolean syncstate = prefs.getBoolean("sync", true);
		if (syncstate) {
			RestApi restApi = RestServiceBuilder.createService(RestApi.class);
			Call<Patient> call = restApi.updatePatient(patient, patient.getUuid(), "full");
			call.enqueue(new Callback<Patient>() {
				@Override
				public void onResponse(Call<Patient> call, Response<Patient> response) {
					if (response.isSuccessful()) {
						Patient updatedPatient = response.body();
						patient.getPerson().setBirthdate(updatedPatient.getPerson().getBirthdate());

						patient.getPerson().setUuid(patient.getUuid());
						/*if (patient.getPerson().getPhoto() != null)
							uploadPatientPhoto(patient);*/

						new PatientDAO().updatePatient(patient.getId(), patient);

						ToastUtil.success("Patient " + patient.getPerson().getName().getNameString()
								+ " updated");
						if (callbackListener != null) {
							callbackListener.onResponse();
						}
					} else {
						ToastUtil.error("Patient " + patient.getPerson().getName().getNameString()
								+ " cannot be updated due to server error" + response.message());
						if (callbackListener != null) {
							callbackListener.onErrorResponse(response.message());
						}
					}
				}

				@Override
				public void onFailure(Call<Patient> call, Throwable t) {
					ToastUtil.notify("Patient " + patient.getPerson().getName().getNameString()
							+ " cannot be updated due to request error: " + t.toString());
					if (callbackListener != null) {
						callbackListener.onErrorResponse(t.getMessage());
					}
				}
			});
		} else {
			ToastUtil.notify("Sync is off. Patient Update data is saved locally " +
					"and will sync when online mode is restored. ");
			if (callbackListener != null) {
				callbackListener.onResponse();
			}
		}
	}

	/**
	 * Download Patient by UUID
	 */
	/*public void downloadPatientByUuid(@NonNull final String uuid, @NonNull final DownloadPatientCallbackListener
	callbackListener) {
		RestApi restApi = RestServiceBuilder.createService(RestApi.class);
		Call<Patient> call = restApi.getPatientByUUID(uuid, "full");
		call.enqueue(new Callback<Patient>() {
			@Override
			public void onResponse(Call<Patient> call, Response<Patient> response) {
				if (response.isSuccessful()) {
					final Patient newPatient = response.body();
					AndroidDeferredManager dm = new AndroidDeferredManager();
					dm.when(downloadPatientPhotoByUuid(newPatient.getUuid())).done(new DoneCallback<Bitmap>() {
						@Override
						public void onDone(Bitmap result) {
							if (result != null) {
								newPatient.getPerson().setPhoto(result);
								callbackListener.onPatientPhotoDownloaded(newPatient);
							}
						}
					});
					callbackListener.onPatientDownloaded(newPatient);
				} else {
					callbackListener.onErrorResponse(response.message());
				}
			}
			
			@Override
			public void onFailure(Call<Patient> call, Throwable t) {
				callbackListener.onErrorResponse(t.getMessage());
			}
		});
	}*/
	
	/*public SimplePromise<Bitmap> downloadPatientPhotoByUuid(String uuid) {
		final SimpleDeferredObject<Bitmap> deferredObject = new SimpleDeferredObject<>();
		RestApi restApi = RestServiceBuilder.createService(RestApi.class);
		Call<ResponseBody> call = restApi.downloadPatientPhoto(uuid);
		call.enqueue(new Callback<ResponseBody>() {
			@Override
			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
				InputStream inputStream;
				if (response.isSuccessful()) {
					inputStream = response.body().byteStream();
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					try {
						inputStream.close();
					} catch (IOException e) {
						logger.e(e.getMessage());
					}
					deferredObject.resolve(bitmap);
				} else {
					Throwable throwable = new Throwable(response.message());
					deferredObject.reject(throwable);
				}
			}
			
			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {
				deferredObject.reject(t);
			}
		});
		return deferredObject.promise();
	}*/
	private void addEncounters(Patient patient) {
		String enc = patient.getEncounters();
		List<Long> list = new ArrayList<>();
		for (String s : enc.split(","))
			list.add(Long.parseLong(s));

		for (long id : list) {
			EncounterCreate encounterCreate = new Select()
					.from(EncounterCreate.class)
					.where("id = ?", id)
					.executeSingle();
			encounterCreate.setPatient(patient.getUuid());
			encounterCreate.save();
			new EncounterService().addEncounter(encounterCreate);
		}
	}

	private SimplePromise<String> getIdGenPatientIdentifier() {
		final SimpleDeferredObject<String> deferred = new SimpleDeferredObject<>();

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(openMrs.getServerUrl() + '/')
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		RestApi apiService =
				retrofit.create(RestApi.class);
		Call<IdGenPatientIdentifiers> call = apiService.getPatientIdentifiers(openMrs.getUsername(), openMrs.getPassword());
		call.enqueue(new Callback<IdGenPatientIdentifiers>() {
			@Override
			public void onResponse(Call<IdGenPatientIdentifiers> call, Response<IdGenPatientIdentifiers> response) {
				IdGenPatientIdentifiers idList = response.body();
				deferred.resolve(idList.getIdentifiers().get(0));
			}

			@Override
			public void onFailure(Call<IdGenPatientIdentifiers> call, Throwable t) {
				ToastUtil.notify(t.toString());
				deferred.reject(t);
			}

		});

		return deferred.promise();
	}

	private SimplePromise<PatientIdentifierType> getPatientIdentifierTypeUuid() {
		final SimpleDeferredObject<PatientIdentifierType> deferred = new SimpleDeferredObject<>();

		RestApi apiService =
				RestServiceBuilder.createService(RestApi.class);
		Call<Results<PatientIdentifierType>> call = apiService.getIdentifierTypes();
		call.enqueue(new Callback<Results<PatientIdentifierType>>() {
			@Override
			public void onResponse(Call<Results<PatientIdentifierType>> call,
					Response<Results<PatientIdentifierType>> response) {
				Results<PatientIdentifierType> idresList = response.body();
				for (PatientIdentifierType result : idresList.getResults()) {
					if (result.getDisplay().equals("OpenMRS ID")) {
						deferred.resolve(result);
						return;
					}
				}
			}

			@Override
			public void onFailure(Call<Results<PatientIdentifierType>> call, Throwable t) {
				ToastUtil.notify(t.toString());
				deferred.reject(t);
			}

		});
		return deferred.promise();
	}

}
