require("ui/components/frame")
require("ui/spellbutton")                                                                 
--TODO I'll start refactoring a bit of the lua once I'm more comfortable with the language!    
--TODO should clean up on some of the code duplication between a lot of these action related lua code files    

-- "CONSTANTS" -------------------------------
local BUTTON_SIZE = 60
local BUTTON_SPACING_X,BUTTON_SPACING_Y = 5,5
local FRAME_WIDTH = 396
local FRAME_HEIGHT = 290 --used for paging later
local BUTTONS_PER_ROW = math.floor(FRAME_WIDTH / BUTTON_SIZE)
-----------------------------------------------

SpellBook = extend({}, Frame)
		
function SpellBook:New()
	local this = Frame:New(uiParent, FRAME_WIDTH, FRAME_HEIGHT, "Spell book", true)
	extend(this, self)
	this.spellButtons = {}

	this:Hide()
	return this
end

function SpellBook:Toggle()
	if(self:IsVisible()) then
		self:Hide()
	else
		self:Show()
	end
end

function SpellBook:AddSpellButton(spell, index)
	local x = (index % BUTTONS_PER_ROW)-1
	local y = math.floor(index / BUTTONS_PER_ROW)
	local castTime = spell:GetCastTime() / 1000

	local contentPanel = self:GetContentPanel()
	local button = SpellButton:New(contentPanel, GetSpellPicture(spell:GetName()), BUTTON_SIZE, BUTTON_SIZE, castTime, spell)
		
	button.spell = spell
	button:SetPoint("TOPLEFT", contentPanel, "TOPLEFT", x* 65 , -y * 65)
	button:Show()
	
	self.spellButtons[index] = button
end



function SpellBook:Clear()
	for i, spellButton in ipairs(self.spellButtons) do
		spellButton:Hide()
		spellButton = nil
	end
end


function SpellBook:Populate()
	local spells = GetAllSpells()
	table.sort(spells, function(a, b)  return a:GetName() > b:GetName() end)
	for i, spell in ipairs(spells) do
		self:AddSpellButton(spell, i)
	end
end


function SpellBook:Update()
   self:Clear()
   self:Populate()
end


playerSpellBook = SpellBook:New()		


RegisterEvent("SPELLCAST_STOPPED", function(event, attacker, spell)
	if IsSelf(attacker) then
		for index, button in ipairs(playerSpellBook.spellButtons) do
			if button.spell == spell then
				button:CancelAction()
			end
		end
	end
end)

RegisterEvent("SPELLCAST_STARTED", function(event, attacker, target, spell)
	if IsSelf(attacker) then
      for index, button in ipairs(playerSpellBook.spellButtons) do
      	if button == spell then
				button:StartCast()
			end
		end
	end
end)


playerSpellBook:Populate()


RegisterEvent("SPELLBOOK_UPDATED", function() playerSpellBook:Update() end)


