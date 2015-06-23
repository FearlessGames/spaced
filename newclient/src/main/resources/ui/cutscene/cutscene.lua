require("lua/core/inheritance")
require("ui/components/picture")
require("ui/components/label")
require("ui/components/container")
require("ui/components/frame")

Cutscene = {}

function Cutscene:New(script, parent)
	local this = extend({}, self)
	this.parent = parent
	this.script = script
	this.fades = {}
	this.animations = {}
	this.zooms = {}
	return this
end

function Cutscene:SetPosition(element, x, y)
	local anchor = element.anchor or "TOPLEFT"
	local parentAnchor = element.parentAnchor or "TOPLEFT"
	element:SetPoint(anchor, self.parent, parentAnchor, x, y)
end

local function handleFades(cutscene)
	for element, values in pairs(cutscene.fades) do
		local t = cutscene.currentTime - values["startTime"]
		element:SetAlpha(values["from"] + t*values["slope"])
		if(values["endTime"] < cutscene.currentTime) then
			element:SetAlpha(values["to"])
			cutscene.fades[element] = nil
		end
	end
end


local function animateSingleStep(cutscene, element, func)
	local x, y, continue = func(cutscene.currentTime)
	cutscene:SetPosition(element, x, y)
	if(continue) then
		cutscene.animations[element] = func
	else
		cutscene.animations[element] = nil
	end
end

local function handleAnimation(cutscene)
	for element, func in pairs(cutscene.animations) do
		animateSingleStep(cutscene, element, func)
	end
end

local function handleZooms(cutscene)
	for element, values in pairs(cutscene.zooms) do
		local t = cutscene.currentTime - values["startTime"]

		local factor =  math.min(t, values["duration"]) *values["rate"]
		element:SetSize(values["width"] * (values["from"] + factor), values["height"] * (values["from"] + factor))
		if(values["endTime"] < cutscene.currentTime) then
			cutscene.zooms[element] = nil
		end
	end
end




function Cutscene:Sleep(millis)
	self.progress = self.progress + millis
	while self.currentTime - self.startTime < self.progress do
		handleZooms(self)
		handleFades(self)
		handleAnimation(self)
		DoLayout(self.parent.base)
		if self.skip then
			self.startTime = self.startTime - millis
			return
		end
		coroutine.yield()
	end
end

-- Use this to insert skip points inside your script
function Cutscene:SkipPoint()
	self.skip = nil
end

-- Call this from a key press to start skipping ahead to the next skip point
function Cutscene:SkipForward()
	self.skip = true
end

function Cutscene:GetDefaultAnchor()
	return "TOPLEFT"
end

function Cutscene:Image(filename)
	local pic = Picture:New(self.parent, filename)
	pic:CenterOn(self.parent)
	pic.anchor = self:GetDefaultAnchor()
	return pic
end

function Cutscene:Text(text, parentComponent)
	local parent = parentComponent or self.parent
	local text = Label:New(parent, text, 12)
	text:CenterOn(parent)
	text.anchor = self:GetDefaultAnchor()
	return text
end

function Cutscene:Fade(element, fromAlpha, toAlpha, time)
	element:SetAlpha(fromAlpha)
	local endTime = self.currentTime + time
	local slope = (toAlpha - fromAlpha) / time
	self.fades[element] = {from = fromAlpha, to = toAlpha, startTime = self.currentTime, endTime = endTime, slope = slope}
end

function Cutscene:Zoom(element, from, to, time)
	local width, height = element:GetSize()
	element:SetSize(width * from, height * from)
	local endTime = self.currentTime + time
	local rate = (to - from) / time
	self.zooms[element] = {startTime = self.currentTime, endTime = endTime, duration = time, rate = rate, width = width, height = height, to = to, from = from}
end

function Cutscene:Animate(element, func)
	animateSingleStep(self, element, func)
end

function Cutscene:LinearMove(element, toX, toY, time)
	local startX, startY = element:GetPoint()
	local startTime = self.currentTime
	self:Animate(element, function(t)
		local elapsed = math.min(t - startTime, time)
		local step = elapsed / time
		return startX + (toX - startX) * step, startY + (toY - startY) * step, elapsed < time
	end)
end

function Cutscene:LinearMoveBy(element, byX, byY, time)
	local startX, startY = element:GetPoint()
	self:LinearMove(element, startX + byX, startY + byY, time)
end


function Cutscene:Start(currentTime)
	self.coro = coroutine.create(self.script)
	self.startTime = currentTime
	self.progress = 0
	self:Resume(currentTime)
end

function Cutscene:Resume(currentTime)
	if coroutine.status(self.coro) == "dead" then
		self.progress = nil
		return
	end

	self.currentTime = currentTime
	assert(coroutine.resume(self.coro, self))
end

CutsceneFrame = {}
function CutsceneFrame:New(parent, script)
	local this = Container:New(parent)
	extend(this, self)
	this:SetSize(uiParent:GetSize())

	this.cutscene = Cutscene:New(script, this)
	this:AddListener("OnUpdate", function()
		if this.cutscene.progress then
			this.cutscene:Resume(GetTime())
		else
			this:RemoveListener("OnUpdate")
			this:Hide()
			this:FireEvent("OnEnd")
		end
	end)
	return this
end

function CutsceneFrame:Start()
	self.cutscene:Start(GetTime())
	self:Show()
end

