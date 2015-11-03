package com.firrael.spring.data;

public class Channel {
	private String name;
	private boolean checked;

	public Channel(String name) {
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
		if (!(obj instanceof Channel))
			return false;

		Channel secondChannel = (Channel) obj;

		return this.name.equals(secondChannel.getName()) && this.getChecked() == secondChannel.getChecked();
	}
}