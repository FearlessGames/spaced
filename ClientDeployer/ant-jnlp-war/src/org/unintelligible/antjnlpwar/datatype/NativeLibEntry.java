package org.unintelligible.antjnlpwar.datatype;

public class NativeLibEntry {
	private String name;
	private String os;
	private String arch;

	public NativeLibEntry(String name, String os, String arch) {

		this.name = name;
		this.os = os;
		this.arch = arch;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getArch() {
		return arch;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}
}
