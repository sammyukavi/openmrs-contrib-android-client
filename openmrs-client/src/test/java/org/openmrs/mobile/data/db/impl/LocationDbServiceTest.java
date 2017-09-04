package org.openmrs.mobile.data.db.impl;

import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.BaseMetadataDbServiceTest;
import org.openmrs.mobile.models.Location;

import java.util.UUID;

public class LocationDbServiceTest extends BaseMetadataDbServiceTest<Location> {
	@Override
	protected BaseDbService<Location> getDbService() {
		return new LocationDbService(new RepositoryImpl());
	}

	@Override
	protected ModelAsserters.MetadataAsserter<Location> getAsserter() {
		return ModelAsserters.LOCATION;
	}

	@Override
	protected Location createEntity() {
		Location entity = new Location();

		entity.setUuid(UUID.randomUUID().toString());
		entity.setName("Location 1");
		entity.setAddress1("Address 1");
		entity.setAddress2("Address 2");
		entity.setCityVillage("City Village");
		entity.setCountry("Country");
		entity.setPostalCode("12345");
		entity.setStateProvince("State Province");

		return entity;
	}
}
