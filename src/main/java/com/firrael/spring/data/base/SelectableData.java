package com.firrael.spring.data.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({ "checked" })
public class SelectableData implements Comparable<SelectableData> {
	private String name;
	private boolean checked;
	private Long count;

	public SelectableData(String name) {
		setName(name);
	}

	public SelectableData(String name, Long count) {
		setName(name);
		setCount(count);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean getChecked() {
		return checked;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SelectableData))
			return false;

		SelectableData secondObject = (SelectableData) obj;

		boolean equals = this.name.equals(secondObject.getName()) && this.getChecked() == secondObject.getChecked();

		return equals;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int compareTo(SelectableData obj2) {
		return obj2.getCount().compareTo(getCount());
	}
}
