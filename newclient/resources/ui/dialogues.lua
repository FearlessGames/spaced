require("ui/uisetup")
require("ui/components/dialogue")
require("ui/unitfunctions")

local function createRessDialogue(parent)
	local dialogue = Dialogue:New(parent, "You are dead!", "gui/ress/deadstonebox", "Return to life")

	dialogue:AddListener("OnAccept", function()
		AcceptRess()
		dialogue:Hide()
	end)

	return dialogue
end


local ressDialogue = createRessDialogue(uiParent)
ressDialogue:CenterOn(uiParent)
ressDialogue:Hide()

RegisterEvent("PLAYER_DIED", function(event)
	ressDialogue:Show()
end)

if not GetSelf():IsAlive() then
	ressDialogue:Show()
end
