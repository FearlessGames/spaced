require("ui/chatmodel")
require("se/hiflyer/moc/moc")


function testSimpleAddLine()
	local chat = ChatModel:New(10)
	local listener = MoC:New()
	chat:AddListener(listener)
	chat:AddLine("foo")
	local text = chat:GetViewableText()
	
	verifyOnce():on(listener):OnUpdate(chat)

	assertEquals("foo", text)
end

function testAdd3LinesOneVisible()
	local chat = ChatModel:New(1)

	chat:AddLine("a")
	chat:AddLine("b")
	chat:AddLine("c")
	assertEquals("c", chat:GetViewableText())
end


function testAdd15Lines()
	local chat = ChatModel:New(10)
	local listener = MoC:New()
	chat:AddListener(listener)
	for i = 1, 15, 1 do
		chat:AddLine("foo" .. i)
	end
	local text = chat:GetViewableText()

	verifyExactly(15):on(listener):OnUpdate(chat)

	assertEquals("foo6\nfoo7\nfoo8\nfoo9\nfoo10\nfoo11\nfoo12\nfoo13\nfoo14\nfoo15", text)
	assertTrue(chat:HasHistory())
	assertTrue(chat:IsAtTail())
end

function testHasHistory()
	local chat = ChatModel:New(2, 1)
	chat:AddLine("a")
	assertFalse(chat:HasHistory())
	chat:AddLine("b")
	assertFalse(chat:HasHistory())
	chat:AddLine("c")
	assertTrue(chat:HasHistory())
	chat:PageUpKeyDown()
	assertFalse(chat:HasHistory())
end

function testPageUpMakesItAllTheWay()
	local chat = ChatModel:New(10, 6)
	for i = 1, 15, 1 do
		chat:AddLine("foo" .. i)
	end
	chat:PageUpKeyDown()
	local text = chat:GetViewableText()
	assertEquals("foo1\nfoo2\nfoo3\nfoo4\nfoo5\nfoo6\nfoo7\nfoo8\nfoo9\n          --- more ---", text)
end

function testPageUpThenPageDown()
	local chat = ChatModel:New(10, 6)
	for i = 1, 15, 1 do
		chat:AddLine("foo" .. i)
	end
	chat:PageUpKeyDown()
	chat:PageDownKeyDown()
	local text = chat:GetViewableText()
	assertEquals("foo6\nfoo7\nfoo8\nfoo9\nfoo10\nfoo11\nfoo12\nfoo13\nfoo14\nfoo15", text)
end


function testPageUpOneJump()
	local chat = ChatModel:New(10, 6)
	for i = 1, 40, 1 do
		chat:AddLine("foo" .. i)
	end
	chat:PageUpKeyDown()
	local text = chat:GetViewableText()
	assertEquals("foo25\nfoo26\nfoo27\nfoo28\nfoo29\nfoo30\nfoo31\nfoo32\nfoo33\n          --- more ---", text)
	assertTrue(chat:HasHistory())
	assertFalse(chat:IsAtTail())
end


function testPageDownAtTailDoesNothing()
	local chat = ChatModel:New(10, 6)
	for i = 1, 15, 1 do
		chat:AddLine("foo" .. i)
	end
	local listener = MoC:New()
	chat:AddListener(listener)

	chat:PageDownKeyDown()
	verifyNever():on(listener):OnUpdate(chat)
	local text = chat:GetViewableText()
	assertEquals("foo6\nfoo7\nfoo8\nfoo9\nfoo10\nfoo11\nfoo12\nfoo13\nfoo14\nfoo15", text)
end

function testPageUpWhenAtTopDoesNothing()
	print("testPageUpWhenAtTopDoesNothing.start")
	local chat = ChatModel:New(10, 6)
	chat:AddLine("Foobar")

	local listener = MoC:New()
	chat:AddListener(listener)

	chat:PageUpKeyDown()
	verifyNever():on(listener):OnUpdate(chat)
	print("testPageUpWhenAtTopDoesNothing.end")
end

function testEndKey()
	local chat = ChatModel:New(10, 6)
	for i = 1, 150, 1 do
		chat:AddLine("foo" .. i)
	end
	for i = 1, 20 do
		chat:PageUpKeyDown() -- to jump up quite a bit
	end
	chat:EndKeyDown() -- and then all the way down
	local text = chat:GetViewableText()
	assertEquals("foo141\nfoo142\nfoo143\nfoo144\nfoo145\nfoo146\nfoo147\nfoo148\nfoo149\nfoo150", text)
end

function testAddLinesWhenNotAtTail()
	local chat = ChatModel:New(10, 6)
	for i = 1, 40, 1 do
		chat:AddLine("foo" .. i)
	end
	chat:PageUpKeyDown()
	chat:AddLine("foo41")
	local text = chat:GetViewableText()
	assertEquals("foo25\nfoo26\nfoo27\nfoo28\nfoo29\nfoo30\nfoo31\nfoo32\nfoo33\n          --- more ---", text)

	chat:EndKeyDown()
	local text = chat:GetViewableText()
	assertEquals("foo32\nfoo33\nfoo34\nfoo35\nfoo36\nfoo37\nfoo38\nfoo39\nfoo40\nfoo41", text)
end
