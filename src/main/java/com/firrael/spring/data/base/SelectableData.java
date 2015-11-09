package com.firrael.spring.data.base;

public class SelectableData {
	private String name;
	private boolean checked;

	public SelectableData(String name) {
		setName(name);
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
}
