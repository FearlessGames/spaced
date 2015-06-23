package se.spaced.client.ardor.ui.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.util.TimeProvider;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.ReturnValues;
import se.spaced.client.environment.time.GameTime;
import se.spaced.client.environment.time.GameTimeManager;
import se.spaced.client.environment.time.GameTimeParser;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.resources.zone.ZoneActivationService;
import se.spaced.shared.resources.zone.Zone;
import se.spaced.shared.world.TimeSystemInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Singleton
public class WorldInfoApi {
	private final UserCharacter userCharacter;
	private final GameTimeManager gameTimeProvider;
	private final TimeProvider timeProvider;
	private final ZoneActivationService zoneActivationService;

	@Inject
	public WorldInfoApi(
			UserCharacter userCharacter,
			GameTimeManager gameTimeProvider,
			ZoneActivationService zoneActivationService,
			TimeProvider timeProvider) {
		this.userCharacter = userCharacter;
		this.gameTimeProvider = gameTimeProvider;
		this.zoneActivationService = zoneActivationService;
		this.timeProvider = timeProvider;
	}

	@LuaMethod(global = true, name = "GetLocation")
	public void getCurrentPosition(ReturnValues returnValues) {
		SpacedVector3 pos = userCharacter.getPosition();
		returnValues.push(pos.getX());
		returnValues.push(pos.getY());
		returnValues.push(pos.getZ());
	}

	@LuaMethod(global = true, name = "GetRotation")
	public void getCurrentRotation(ReturnValues returnValues) {
		SpacedRotation rot = userCharacter.getRotation();
		returnValues.push(rot.getW());
		returnValues.push(rot.getX());
		returnValues.push(rot.getY());
		returnValues.push(rot.getZ());
	}

	@LuaMethod(global = true, name = "GetTimeOfDay")
	public String getCurrentTimeOfDay() {
		GameTime time = gameTimeProvider.fromSystemTime(timeProvider.now());
		TimeSystemInfo timeInfo = gameTimeProvider.getTimeInfo();
		return GameTimeParser.toString(time, timeInfo);
	}

	@LuaMethod(global = true, name = "GetZones")
	public List<Zone> getActiveZones() {
		return zoneActivationService.getActiveZones(userCharacter.getPosition());
	}

	@LuaMethod(global = true, name = "FormatTime")
	public String formatTime(Date date) {
		return new SimpleDateFormat("HH:mm:ss").format(date);
	}
}