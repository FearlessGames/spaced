require("ui/uisetup")
require("ui/components/actionbutton")
require("ui/components/tooltip")
require("ui/components/progresscircle")
require("ui/components/label")

local superTooltip = SpellTooltip:New(uiParent)

--
-- Spell button - extends ActionButton
--

local mouseDownTimeToStartDrag = 0.4

SpellButton = extend({}, ActionButton)

function SpellButton:New(parent, iconFile, width, height, actionTime, spell, actionSlot)
	local this = ActionButton:New(parent, iconFile, width, height)
	extend(this, self)

	local icon = this.icon
	local button = this.button

	this.progress = ProgressCircle:New(this, icon:GetWidth(), icon:GetHeight())
	this.progress:SetColor(0.0, 0.0, 0.0, 0.4)
	this.progress:Hide()
	this.cooldownText = Label:New(this, "", 20)
	this.cooldownText:SetPoint("TOPLEFT", this, "TOPLEFT", 0, 0);
	this.cooldownText:SetColor(1, 1, 1, 1)
	this.cooldownText:BringToFront()

	this.state = "ACTIVE"
	this.actionTime = actionTime
	this.spell = spell

	local mouseDownTime = 0
	local isDragging = false
	local isMouseDown = false

	local function onMouseDown(self)
		isDragging = false
		isMouseDown = true
		mouseDownTime = 0
	end
	local function onMouseLeave(self)
   	isDragging = false
		isMouseDown = false
		mouseDownTime = 0
	end

	local function onUpdate(self, timeElapsed)
   	if isDragging ~= true and isMouseDown and mouseDownTimeToStartDrag < timeElapsed + mouseDownTime  then
   		PickUpSpell(spell)
			if(actionSlot ~= nil) then
				self:Hide()
				actionSlot:SetActionButton(nil)
			end		
			isDragging = true
   	elseif isMouseDown then
      	mouseDownTime = mouseDownTime + timeElapsed
   	end
	end

	this:AddListener("OnUpdate", onUpdate)

	button:AddListener("OnMouseDown", onMouseDown)
	button:AddListener("OnMouseUp", onMouseLeave)
	button:AddListener("OnMouseLeave", onMouseLeave)

	local function onProgressUpdate(self, timeElapsed)
		this:OnProgressUpdate(timeElapsed)
	end

	this:AddListener("OnUpdate", onProgressUpdate)

	this.button:AddListener("OnMouseDragEnter", function(target)
		if(actionSlot ~= nil) then
			actionSlot.button:FireEvent("OnMouseDragEnter", target)
		end
	end)
	this.button:AddListener("OnMouseDragLeave", function(target)
		if(actionSlot ~= nil) then
			actionSlot.button:FireEvent("OnMouseDragEnter", target)
		end
	end)
	this.button:AddListener("OnMouseDragDrop", function(target)
		if(actionSlot ~= nil) then
			actionSlot.button:FireEvent("OnMouseDragEnter", target)
		end
	end)

	-- ToolTip
	if spell then
		this.button:AddListener("OnMouseEnter", function(self)
			superTooltip:ShowSpell(spell)
		end)
		this.button:AddListener("OnMouseLeave", function(self)
			superTooltip:Hide()
		end)
	end

	return this
end

 -- Fired when server alerts of cast start
function SpellButton:StartCast()
	self:FireEvent("StartCast")
	self.progress:Show()
	self.progress:SetProgress(1)
	self.state = "CASTING"
end

-- Fired on user input
function SpellButton:StartAction()
	self:FireEvent("StartAction")
	if self.spell then
		Cast(self.spell)
	end
end

function SpellButton:CancelAction()
	self:FireEvent("CancelAction")
	self.state = "ACTIVE"
	self.progress:Hide()
	self.progress:SetProgress(0)
end

function SpellButton:OnClick(button)
	if self.state == "ACTIVE" then
		self:StartAction()
	end
end

function SpellButton:StartCooldown(elapsed, totalTime)
	if totalTime <= 0 then
		return self:StopCooldown()
	end

	self.cooldownTime = totalTime
	self.progress:SetProgress(0)

	self:FireEvent("PerformAction")
	self.button:Disable()
	self.button:SetColor(1, 0.0, 0.0, 1)
	self.state = "COOLDOWN"
	self.icon:SetColor(0.7, 0.7, 0.7, 1)

	self:Cooldown(elapsed)
end

function SpellButton:StopCooldown()
	self:FireEvent("ActionReady")
	self.cooldownText:SetText("")
	self.state = "ACTIVE"
	self.progress:Hide()
	self.button:Enable()
	self.button:SetColor(1, 1, 1, 1)
	self.icon:SetColor(1, 1, 1, 1)
end

function SpellButton:StopCasting()
    self.state = "ACTIVE"
    self.progress:Hide()
    self.progress:SetProgress(0)
    self.button:Enable()
end

function SpellButton:OnProgressUpdate(timeElapsed)
	if self.state == "CASTING" then
		self:Casting(timeElapsed)
	elseif self.state == "COOLDOWN" then
		self:Cooldown(timeElapsed)
	end
end

function SpellButton:Casting(timeElapsed)
	local p = self.progress:GetProgress() - (timeElapsed / self.actionTime)
	if p <= 0 then
		p = 0
        self:StopCasting()
	end
	self.progress:SetProgress(p)
end

function SpellButton:Cooldown(timeElapsed)
	local p = self.progress:GetProgress() + (timeElapsed / self.cooldownTime)
	if p > 1 then
		p = 1
		self:StopCooldown()
	else
		self.progress:SetProgress(p)
		local totalTime = self.cooldownTime
		local timeWaiting = self.progress:GetProgress() * totalTime
		local s = string.format("%.1f", math.ceil(10 * (totalTime - timeWaiting)) / 10)
		--local s = totalTime .. ":" .. timeWaiting
		self.cooldownText:SetText(s)
	end
end
