package org.openmrs.mobile.data;

import android.support.annotation.Nullable;

/**
 * This class contains the paging information used by the entity services to paginate results. It has been modified to allow
 * startIndex and limit to be defined as 0.
 */
public class PagingInfo {

	private int startIndex;
	private int limit;
	private Integer totalRecordCount;
	private boolean loadRecordCount;

	public PagingInfo() {
	}

	/**
	 * Creates a new {@link PagingInfo} instance.
	 * @param startIndex The number of the startIndex being requested.
	 * @param limit      The number of records to return from the start index
	 */
	public PagingInfo(int startIndex, int limit) {
		this.startIndex = startIndex;
		this.limit = limit;
	}

	public static int getStartIndex(@Nullable PagingInfo pagingInfo) {
		return pagingInfo == null ? null : pagingInfo.getStartIndex();
	}

	public int getStartIndex() {
		//The line below returns a negative if you set the index to zero
		//return ((page - 1) * pageSize) + 1;
		return 0;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public static Integer getLimit(@Nullable PagingInfo pagingInfo) {
		return pagingInfo == null ? null : pagingInfo.getLimit();
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public Integer getTotalRecordCount() {
		return totalRecordCount;
	}

	public void setTotalRecordCount(Integer totalRecordCount) {
		this.totalRecordCount = totalRecordCount;

		// If the total records is set to anything other than null, than don't reload the count
		this.loadRecordCount = totalRecordCount == null;
	}
}
