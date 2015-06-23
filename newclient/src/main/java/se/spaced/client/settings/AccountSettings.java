package se.spaced.client.settings;

import se.krka.kahlua.integration.annotations.LuaMethod;

public class AccountSettings implements ValidatedSettings {
	public static final String USE_OLD_PASSWORD = "USEOLDPASSWORD";

	private String account = "";
	private String hashedPassword = "";
	private boolean autoLogin = false;
	private String autoLoginServer = "127.0.0.1";
	private int autoLoginPort = 0;
	private String autoLoginCharacter = "";


	@LuaMethod
	public boolean getAutoLogin() {
		return autoLogin;
	}

	@LuaMethod
	public void setAutoLogin(boolean enabled) {
		autoLogin = enabled;
	}

	@LuaMethod
	public String getAutoLoginServer() {
		return autoLoginServer;
	}

	@LuaMethod
	public void setAutoLoginServer(String server) {
		autoLoginServer = server;
	}

	@LuaMethod
	public String getAutoLoginCharacter() {
		return autoLoginCharacter;
	}

	@LuaMethod
	public void setAutoLoginCharacter(String character) {
		autoLoginCharacter = character;
	}

	@LuaMethod
	public int getAutoLoginPort() {
		return autoLoginPort;
	}

	@LuaMethod
	public void setAutoLoginPort(int autoLoginPort) {
		this.autoLoginPort = autoLoginPort;
	}

	@LuaMethod
	public String getAccount() {
		return account;
	}


	public void setAccount(String account) {
		this.account = account;
	}

	@LuaMethod
	public String getPassword() {
		if (hashedPassword == null || hashedPassword.isEmpty()) {
			return "";
		}
		return USE_OLD_PASSWORD;
	}

	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	@Override
	public boolean valid() {
		return true;
	}
}
