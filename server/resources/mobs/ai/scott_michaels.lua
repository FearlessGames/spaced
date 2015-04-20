local self = ...

local aggro = self:GetAggroManager()
local spellBook = self:GetSpellBook()

local now = 0

local myspell = spellBook:GetSpell("Pocket rocket")
local hardBlow  = spellBook:GetSpell("Hard blow")

assert(myspell, "Could not find pocket rocket (that's what she said!")
assert(hardBlow, "Could not find hard blow (that's what she said!")

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

local function chase(target)
	print "Scott enters chase mode"
	local t1 = now
	while now - t1 < 12*1000 do
		if not aggro:IsAggroWith(target) then
			break
		end
		local inrange = IsInRangeForSpell(self, target, hardBlow)
		if inrange == "TOO_FAR_AWAY" then
			WalkTo(self, target:GetPosition())
			decided()
		else
			local castTime = hardBlow:GetCastTime()
			CastSpell(self, target, hardBlow)
			if castTime then
				sleep(castTime)
			end
			break
		end
	end
end

local function normalmode()
	print "Scott enters normal mode"
	local t1 = now
	while now - t1 < 10*1000 do
		local target = aggro:GetMostHated()
		if not target then
			break
		end
		local inrange = IsInRangeForSpell(self, target, myspell)
		if inrange == "TOO_FAR_AWAY" then
			WalkTo(self, target:GetPosition())
			decided()
		elseif inrange == "TOO_CLOSE" then
			local start = myspell:GetRanges():GetStart()
			BackAwayFrom(self, target:GetPosition(), start)
			decided()
		else
			StopWalking(self)
			LookAt(self, target)
			local castTime = myspell:GetCastTime()
			CastSpell(self, target, myspell)
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
		local target = aggro:GetRandomHated()
		if target then
			Say(self, "I will kill you " .. target:GetName() .. "!")
			chase(target)
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

local words = {"hard", "long", "huge", "big"}
function events.playerSaid(fromName, message)
	local matches = false
	for k, v in ipairs(words) do
		 matches = matches or message:find(v, 1, 1)
	end
	if(matches) then
		Emote(self, "sound/emotes/scott/shesaid.wav", "That's what SHE said!")
	end
end

return act, events
