require("ui/unitfunctions")
require("se/hiflyer/moc/moc")

function table.find(f, l) -- find element v of l satisfying f(v)
  for _, v in ipairs(l) do
    if f(v) then
      return v
    end
  end
  return nil
end

function table.makeSet(t) -- set of list
  local u = { }
  for _, v in ipairs(t) do u[v] = true end
  return u
end

local SELF = MoC:New()
local TARGET = MoC:New()
local TOT = MoC:New()

when(SELF:GetTarget()):thenReturn(TARGET)

function GetEntityFromUnitId(id)
	if(id == "player") then
		return SELF
	end
end

function testResolveSelf()
	local results = ResolveUnitIds(GetSelf())
	assertEquals(1, #results)
	assertNotNil(table.find(function(v) return v == "player" end, results))
end

function testResolveNone()
	local results = ResolveUnitIds({})
	assertEquals(0, #results)
end

function testResolveTarget()
	local results = ResolveUnitIds(GetTarget())
	for k, v in pairs(results) do
		print(k, v)
	end
	assertEquals(1, #results)
	assertNotNil(table.find(function(v) return v == "target" end, results))
end

function testResolveTot()
	when(TARGET:GetTarget()):thenReturn(TOT)

	local results = ResolveUnitIds(GetToT())
	assertEquals(1, #results)
	assertNotNil(table.find(function(v) return v == "targettarget" end, results))
end

function testResolveWhenTargetingSelf()

	local SELF_MOC_WORKAROUND = MoC:New()

	when(SELF_MOC_WORKAROUND:GetTarget()):thenReturn(SELF_MOC_WORKAROUND)

	function GetSelf()
		return SELF_MOC_WORKAROUND
	end

	local results = ResolveUnitIds(GetSelf())
	assertEquals(3, #results)
	assertNotNil(table.find(function(v) return v == "player" end, results))
	assertNotNil(table.find(function(v) return v == "target" end, results))
	assertNotNil(table.find(function(v) return v == "targettarget" end, results))
end

function testFocus()
	SetFocusTarget(TARGET)
	local results = ResolveUnitIds(GetTarget())
	assertEquals(2, #results)
	assertNotNil(table.find(function(v) return v == "focus" end, results))
	assertNotNil(table.find(function(v) return v == "target" end, results))
	SetFocusTarget(nil)
end

function testResolveNil()
	local results = ResolveUnitIds(nil)
	assertEquals(0, #results)
end