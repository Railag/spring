package com.firrael.spring.data;

public class Category {

	private String name;
	private boolean checked;

	public Category(String name) {
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
		if (!(obj instanceof Category))
			return false;

		Category secondCategory = (Category) obj;

		return this.name.equals(secondCategory.getName()) && this.getChecked() == secondCategory.getChecked();
	}
}