require("ui/sct")
require("ui/mainactionbar")

local spells = nil

local function setupSpells(event)
	playerActionbar:Clear()
	spells = GetAllSpells()
	table.sort(spells, function(a, b)  return a:GetName() > b:GetName() end)
	for i, spell in ipairs(spells) do
		playerActionbar:AddSpellButton(i, spell)
	end
end

local spellPictures = {}
spellPictures["Lazor blast"] = "textures/gui/abilityicons/lazor_blast.png"
spellPictures["Lazor pulse"] = "textures/gui/abilityicons/lazor_pulse.png"
spellPictures["Strike"] = "textures/gui/abilityicons/melee.png"
spellPictures["Overcharge"] = "textures/gui/abilityicons/overcharge.png"
spellPictures["Volatile combustion"] = "textures/gui/abilityicons/volatile_combustion.png"
spellPictures["Plasma ball"] = "textures/gui/abilityicons/plasma_ball.png"
spellPictures["Recharge"] = "textures/gui/abilityicons/recharge.png"
spellPictures["Heal"] = "textures/gui/abilityicons/heal.png"
spellPictures["Nail"] = "textures/gui/abilityicons/nailed.png"
spellPictures["Disrupt"] = "textures/gui/icons/IconMeleePathDefence.png"
spellPictures["Torch"] = "textures/gui/abilityicons/torch.png"
spellPictures["Mend"] = "textures/gui/abilityicons/mend.png"
spellPictures["GM Deathtouch"] = "textures/gui/abilityicons/deathtouch.png"

local spellLolLore = {}
spellLolLore["Lazor blast"] = "Shoots lazers pew pew"
spellLolLore["Lazor pulse"] = "A weak ranged attack"
spellLolLore["Strike"] = "Strike at enemy physically"
spellLolLore["Torch"] = "Sets the target on fire,\ndealing fire damage over time"
spellLolLore["Mend"] = "Heal the target over time"
spellLolLore["Recharge"] = "Recharges your shield"
spellLolLore["Nail"] = "Nails the target to the ground"

function GetSpellFlavour(name)
	return spellLolLore[name]
end

function GetSpellByName(name)
   for i, spell in ipairs(spells) do
   	if(spell:GetName() == name) then
   	   return spell
   	end
   end
   
   return nil
end

function GetSpellPicture(name)
	local pictureName = spellPictures[name]
	if (pictureName == nil) then
		pictureName = "textures/gui/icons/IconUnknown"
	end
	return pictureName
end

local currentlyPickedUpSpellIcon = nil
local currentlyPickedUpSpell = nil
local queuedSpell = nil

function PickUpSpell(spell)
	local spellName = spell:GetName()
	if (currentlyPickedUpSpellIcon ~= nil) then
	   return 
	end
   local icon = Picture:New(uiParent, GetSpellPicture(spellName))
	icon:SetSize(64, 64)
   icon:SetPoint("MIDCENTER", uiParent, "MIDCENTER", 0, 0)
   icon:SetCanBeActive(true)
   icon:SetDraggable(true)

   StartDrag(icon)
   
   currentlyPickedUpSpellIcon = icon
   currentlyPickedUpSpell = spellName
   icon:AddListener("OnMouseDragStop", function(self)
   	currentlyPickedUpSpellIcon:Hide()
		currentlyPickedUpSpellIcon = nil
		if(queuedSpell ~= nil) then
			PickUpSpell(queuedSpell)
			queuedSpell = nil
		end
   end)
end

function QueueSpellPickUp(spell)
	queuedSpell = spell
end


function GetPickedUpSpell()
    return currentlyPickedUpSpell
end

function ClearCursor()
	if(currentlyPickedupUp ~= nil) then
		currentlyPickedupUp:Hide()
		currentlyPickedupUp = nil
	end
end

function PickupAction(actionId)

end

function PlaceAction(slot)

end


RegisterEvent("SPELLBOOK_UPDATED", setupSpells)

-- just in case the spells got updated before this code was loaded.
setupSpells()

