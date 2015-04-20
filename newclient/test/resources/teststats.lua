require("ui/util/stats")
require("ui/lang/stats")


function testIterator()
	local order = {}

	for statType in statTypes() do
		table.insert(order, statType)
	end

	assertEquals(7, #order)

	assertEquals("ATTACK_RATING", order[1])
	assertEquals("STAMINA", order[2])
	assertEquals("SHIELD_CHARGE", order[3])
	assertEquals("SHIELD_EFFICIENCY", order[4])
	assertEquals("SHIELD_RECOVERY", order[5])
	assertEquals("COOL_RATE", order[6])
	assertEquals("SPEED", order[7])
end

function testStaminaName()
	local dispName = GetStatDisplayName("STAMINA")

	assertNotNil(dispName)
	assertEquals("Stamina", dispName)
end

function testNonExistingStatName()
	local dispName = GetStatDisplayName("AWESOMENESS")

	assertNotNil(dispName)
	assertEquals("AWESOMENESS", dispName)
end

function testStaminaDesc()
	local desc = GetStatDescription("STAMINA")

	assertNotNil(desc)
end

function testNonExistingStatDesc()
	local desc = GetStatDescription("AWESOMENESS")

	assertNotNil(desc)
end