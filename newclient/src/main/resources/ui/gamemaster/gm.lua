require("ui/textinputhandler")

if not IsGm() then
	GM_Visit = nil
	GM_Teleport = nil
	GM_ForceException = nil
	GM_ShowAddPropDialog = nil
	GM_GiveItem = nil
	GM_GrantSpell = nil
	GM_GiveMoney = nil
	GM_ReloadMob = nil
	GM_SpawnMob = nil
	GM_ReloadServerContent = nil
	GM_AiInfo = nil
	GM_Summon = nil

	UnregisterSlashCommand("/addareaprop")
	UnregisterSlashCommand("/addlocationprop")
	UnregisterSlashCommand("/addregionprop")
	UnregisterSlashCommand("/dropdown")
	UnregisterSlashCommand("/helicopter")
	UnregisterSlashCommand("/npe")
	UnregisterSlashCommand("/spd")
	UnregisterSlashCommand("/teleport")
	UnregisterSlashCommand("/visit")
end
