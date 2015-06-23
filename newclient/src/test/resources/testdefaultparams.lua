require("ui/util/defaultparams")

function testAllDefaultSimple()
	local d = {
		a = 3,
		b = 5
	}

	local f = function(x)
		setDefault(x, d)
		assertEquals(3, x.a)
		assertEquals(5, x.b)
	end
	f{}
end

function testOverrideOneSimple()
	local d = {
		a = 3,
		b = 5
	}

	local f = function(x)
		setDefault(x, d)
		assertEquals(4, x.a)
		assertEquals(5, x.b)
	end
	f{a = 4}
end

function testUseDefaultsAndAddExtreSimple()
	local d = {
		a = 3,
		b = 5
	}

	local f = function(x)
		setDefault(x, d)
		assertEquals(3, x.a)
		assertEquals(5, x.b)
		assertEquals(7, x.c)
	end
	f{c = 7}
end

function testAllDefaultNested()
	local d = {
		a = 3,
		b = {
			foo = "hepp",
			bar = "hopp"
		}
	}

	local f = function(x)
		setDefault(x, d)
		assertEquals(3, x.a)
		assertEquals("hepp", x.b.foo)
		assertEquals("hopp", x.b.bar)
	end
	f{}
end

function testOverrideOneNested()
	local d = {
		a = 3,
		b = {
			foo = "hepp",
			bar = "hopp"
		}
	}

	local f = function(x)
		setDefault(x, d)
		assertEquals(3, x.a)
		assertEquals("hepp", x.b.foo)
		assertEquals("baz", x.b.bar)
	end
	f{b = {bar = "baz"}}
end

function testParamIsNil()
	local d = {
		a = 3,
		b = 5
	}

	local f = function(x)
		x = setDefault(x, d)
		assertEquals(3, x.a)
		assertEquals(5, x.b)
	end
	f()
end

function testIndexMetaIsAlreadySet()
	print("###########")
	local d = {
		a = 3,
		b = 5
	}
	local p = {
		a = 6
	}

	local f = function(x)
		setDefault(x, p)
		setDefault(x, d)

		assertEquals(6, x.a)
		assertEquals(5, x.b)
	end

	f{}
	print("###########")
end

function testComponentUseCase()
	local params =	{
		height={
			initial=800
		},
		width={
			initial=1280
		}
	}
	local defaultParams =	{
		height={
			initial=0,
			max=math.huge,
			min=0
		},
		width={
			initial=0,
			max=math.huge,
			min=0
		}
	}
	local p = setDefault(params, defaultParams)

	print(pp(p))

	assertEquals(1280, p.width.initial)
	assertEquals(800, p.height.initial)
	assertEquals(0, p.width.min)
	assertEquals(0, p.height.min)
	assertEquals(math.huge, p.width.max)
	assertEquals(math.huge, p.height.max)
end

function testDefaultIsNil()

	local f = function(x)
		setDefault(x, nil)
		assertEquals(2, x.a)
		assertEquals("baz", x.b.bar)
	end
	f{a = 2, b = {bar = "baz"}}
end
