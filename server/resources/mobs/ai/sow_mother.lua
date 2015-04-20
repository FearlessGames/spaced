local self = ...

local aggro = self:GetAggroManager()
local spellBook = self:GetSpellBook()

local now = 0

local gnaw = spellBook:GetSpell("Gnaw")
local crushingBite = spellBook:GetSpell("Crushing Bite")

assert(gnaw, "Could not find Gnaw")
assert(crushingBite, "Could not find Crushing Bite")

local function yield(v)
	return coroutine.yield(v)	
end

local function decided(v)
	return yield(true)
end

local function undecided(v)
	return yield(false)
end

local function sleep(t)
	local t1 = now
	while now - t1 <= t do
		decided()
	end
end

local function waitForTarget()
	while true do
		local target = aggro:GetMostHated()
		if target then
			return target
		end
		undecided()
	end
end

local function bite(target)
	local t1 = now
    print("Sow enters bite mode " .. t1)
    while now - t1 < 12*1000 do
		if not aggro:IsAggroWith(target) then
			break
		end
		local inrange = IsInRangeForSpell(self, target, crushingBite)
		if inrange == "TOO_FAR_AWAY" then
			print("Charging!")
			MoveTo(self, target:GetPosition(), "CHARGE")
			decided()
		else
			print("Casting crushing bite!")
			local castTime = crushingBite:GetCastTime()
			CastSpell(self, target, crushingBite)
			if castTime then
				sleep(castTime)
			end
			break
		end
	end
end

local function normalmode()
	print "Sow enters normal mode"
	local t1 = now
	while now - t1 < 10*1000 do
		local target = aggro:GetMostHated()
		if not target then
			break
		end
		local inrange = IsInRangeForSpell(self, target, gnaw)
		if inrange == "TOO_FAR_AWAY" then
			RunTo(self, target:GetPosition())
			decided()
		elseif inrange == "TOO_CLOSE" then
			local start = gnaw:GetRanges():GetStart()
			BackAwayFrom(self, target:GetPosition(), start)
			decided()
		else
			StopWalking(self)
			LookAt(self, target)
			local castTime = gnaw:GetCastTime()
			CastSpell(self, target, gnaw)
			if castTime then
				sleep(castTime)
			end
		end
	end
end

local coro = coroutine.create(function()
	while true do
		local target = waitForTarget()
		normalmode()
		local mostHated = aggro:GetMostHated()
		local target = aggro:GetRandomHated(mostHated) or mostHated
		if target then
            print("Focus on " .. target:GetName())
            Say(self, "Oink!")
			bite(target)
        else
            print("No focus")
		end
	end
end)

function act(t)
	now = t
	if coroutine.status(coro) == "dead" then
		return false
	end
	local ok, a, b, c = coroutine.resume(coro)
	if not ok then
		error(a, b, c)
	end
	return a
end

events = {}

function events.combatStatusChanged(enter)
	if(not enter) then
		StopWalking(self)
	end
end


return act, events
