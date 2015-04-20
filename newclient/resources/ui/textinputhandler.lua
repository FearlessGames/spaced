require("ui/chatmodel")
require("ui/unitfunctions")

local lookup = {}
local sorted = {}

function split(s, count)
	count = count or 1000
	if s then
		s = s:match("^%s*(.*)$")
		if count <= 1 then
			return s
		end
		--local s1, s2 = s:match("([^ ])+ +(.*)")
		local s1, s2 = s:match("^(%S+)%s+(.*)$")
		if s1 then
			return s1, split(s2, count - 1)
		end
		return s
	end
end

function HandleTextInput(s)
	if #s > 0 then
		if s:sub(1, 1) == "/" then
			local s1, s2 = split(s, 2)
			if s1 then
				local command = lookup[s1]
				if command then
					command.fun(s1, s2)
				else
					--print(s1 .. " is not a valid command. Try /help")
					chatModel:AddLine(s1 .. " is not a valid command. Try /help")
				end
			end
		else
			Say(s)
		end
	else
		-- TODO: remove focus from editBox 
	end
end

function RegisterSlashCommand(fun, description, primary, ...)
	local command = {fun = fun, description = description, primary = primary, secondary = {...}}

	table.insert(sorted, primary)
	table.sort(sorted)

	lookup[primary] = command
	for k, slash in ipairs(command.secondary) do
		lookup[slash] = command
	end
end

function UnregisterSlashCommand(name)
	for i, v in ipairs(sorted) do
		if(v == name) then
			table.remove(sorted, i)
			break
		end
	end
	local command = lookup[name]
	for k, v in pairs(lookup) do
		if(v == command) then
			lookup[k] = nil
		end
	end

end

 local function help(command, params)
	chatModel:AddLine("")
 	chatModel:AddLine("Available commands:")
	for index, primary in ipairs(sorted) do
		local command = lookup[primary]
		local fun = command.fun
		local description = command.description
		if fun then
			local extra = table.concat(command.secondary, ", ")
			if extra ~= "" then
				extra = " (" .. extra .. ")"
			end
			print(string.format("%s: %s%s", primary, description, extra))
			chatModel:AddLine(string.format("%s: %s%s", primary, description, extra))
		end
	end
end

local teleport = function(cmd, s)
	print(cmd)
	print(s)
	local x, y, z = split(s, 3)
	Teleport(tonumber(x), tonumber(y), tonumber(z))
end

local useHelicopter = function(cmd, s)
	print("calling SetHelicopterMode") 
	SetHelicopterMode()
end

local doDance = function(cmd, s)
   Dance()
end

local doSit = function(cmd, s)
   Sit()
end

local doSleep = function(cmd, s)
   Sleep()
end

local location = function(cmd, s)
	local x, y, z = GetLocation()
	chatModel:AddLine(string.format("Your current location is [%f, %f, %f]", x, y, z))
end

local reloadZone = function(cmd, s)
	ReloadZone()
end

local reloadZoneIndex = function(cmd, s)
	ReloadZoneIndex()
end

local reloadMigrators = function(cmd, s)
	ReloadMigrators()
end

local showZoneIndexShapes = function(cmd, s)
	if s == "true" then
		ShowZoneIndexShapes(true)
	end
	if s == "false" then
		ShowZoneIndexShapes(false)
	end 
end

local addPropToCurrentRegion = function(cmd,s)
	local xRot, yRot, zRot, wRot = GetRotation()
	local x, y, z = GetLocation()
	local addedOk = AddRegionProp("static/xmo/props/"..s, point(x,y,z), point(1,1,1), quaternion(xRot,yRot,zRot,wRot))
	if addedOk == false then
		chatModel:AddLine("could not locate prop")
	end
end

local addPropToCurrentArea = function(cmd,s)
	local xRot, yRot, zRot, wRot = GetRotation()
	local x, y, z = GetLocation()
	local addedOk = AddAreaProp("static/xmo/props/"..s, point(x,y,z), point(1,1,1), quaternion(xRot,yRot,zRot,wRot))
	if addedOk == false then
		chatModel:AddLine("could not locate prop")
	end
end

local addPropToCurrentLocation = function(cmd,s)
	local xRot, yRot, zRot, wRot = GetRotation()
	local x, y, z = GetLocation()
	local addedOk = AddLocationProp("static/xmo/props/"..s, point(x,y,z), point(1,1,1), quaternion(xRot,yRot,zRot,wRot))
	if addedOk == false then
		chatModel:AddLine("could not locate prop")
	end
end

local ShowPropDialog = function(cmd, s)
	GM_ShowAddPropDialog()
end

local ShowCreateAreaDialogue = function(cmd, s)
	GM_ShowCreateAreaDialogue()
end

local Npe = function(cmd, s)
	if(s == "true") then
		ForceException(true)
	else
		ForceException(false)
	end
end

local invalidateMaterialCache = function(cmd, s)
	InvalidateMaterialCache()
	ReloadZone()
end


local rotation = function(cmd, s)
	local x, y, z, w = GetRotation()
	chatModel:AddLine(string.format("Your current rotation is [%f, %f, %f %f]", x, y, z, w))
end


local dropdown = function(cmd, s)
	local x, y, z = GetLocation()
	y = y + 1000
	Teleport(tonumber(x), tonumber(y), tonumber(z))
end

local reloadFx = function(cmd, s)
	ReloadFx()
end

local visit = function(cmd, s)
	Visit(s)
end

local equip = function(cmd, s)
	local where, what = split(s, 2)
	Equip(where, what)
end

local unequip = function(cmd, s)
	Unequip(s)
end

local GmGetItem = function(cmd, s)
	local count, item = s:match("^(%d+) (.*)$")
	if not count then
		count = 1
		item = s
	end
	GM_GiveItem(GetSelf():GetName(), item, count)
end

RegisterSlashCommand(help, "Shows the list of commands", "/help", "/h")
RegisterSlashCommand(function() ReloadUi() end, "Reloads the UI", "/reloadui", "/rl")
RegisterSlashCommand(teleport, "Teleport to given position", "/teleport", "/tel")
RegisterSlashCommand(location, "Prints your position", "/location", "/loc")
RegisterSlashCommand(reloadFx, "Reloads effects", "/rlfx")
RegisterSlashCommand(rotation, "Prints your position", "/rotation", "/rot")
RegisterSlashCommand(dropdown, "Drops you down from 1000m up", "/dropdown", "/dd")
RegisterSlashCommand(visit, "Lets you visit your friend", "/visit", "/vis")
RegisterSlashCommand(reloadZone, "Reloads the static meshes in the current zone" , "/reloadZone", "/rz")
RegisterSlashCommand(reloadZoneIndex, "Reloads the zone index file" , "/reloadZoneIndex", "/ri")
RegisterSlashCommand(showZoneIndexShapes, "Shows or hides debug graphics for zones", "/showZoneIndexShapes", "/szi") 
RegisterSlashCommand(addPropToCurrentRegion, "Add a prop with the players current rotation and location", "/addregionprop", "/arp")
RegisterSlashCommand(addPropToCurrentArea, "Add a prop with the players current rotation and location", "/addareaprop", "/aap")
RegisterSlashCommand(addPropToCurrentLocation, "Add a prop with the players current rotation and location", "/addlocationprop", "/alp")
RegisterSlashCommand(invalidateMaterialCache, "Clear the material cache if disk changes has occured", "/invalidateMCache", "/imc")
RegisterSlashCommand(useHelicopter, "Uses your helicopter", "/helicopter", "/hel")
RegisterSlashCommand(doDance, "Makes player dance", "/dance")
RegisterSlashCommand(doSit, "Makes player sit", "/sit")
RegisterSlashCommand(doSleep, "Makes player sleep", "/sleep")
RegisterSlashCommand(equip, "Make player equip xmo", "/equip", "/eq")
RegisterSlashCommand(unequip, "Make player unequip xmo", "/unequip", "/ueq")

RegisterSlashCommand(StartProfiler, "Starts the profiler", "/pstart")
RegisterSlashCommand(StopProfiler, "Stops the profiler", "/pstop")
RegisterSlashCommand(ResetProfiler, "Resets the profiler", "/preset")
RegisterSlashCommand(DumpProfiler, "Dumps the profiler output to file", "/pdump")

RegisterSlashCommand(ShowPropDialog, "Shows the prop dialog", "/spd")
RegisterSlashCommand(ShowCreateAreaDialogue, "Shows the create area dialogue", "/scad")
RegisterSlashCommand(Npe, "Induce NPE in the server", "/npe")
RegisterSlashCommand(GmGetItem, "Grants you an item", "/gief", "/gi")
		
RegisterSlashCommand(function() playerSpellBook:Toggle() end, "Toggles the spell book", "/spellbook", "/sb")
RegisterSlashCommand(function() playerSpellBook:Update() end, "Forced update the spell book", "/updatespellbook", "/usb")

RegisterSlashCommand(function() playerInventory:Toggle() end, "Toggles the inventory", "/inv")
