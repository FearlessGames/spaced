require("ui/dragdrop")
require("se/hiflyer/moc/moc")

function testCreate()
	local parent = MoC:New()
	local dnd = DragDrop:New(parent)
	
	assertFalse(dnd:IsDragging())
end

function testStartDrag()
	local parent = MoC:New()
	local dnd = DragDrop:New(parent)

	GetMousePosition = CallHandler:New()
	GetMousePosition:AddStub({}, {30, 40})

	local comp = MoC:New()
	when(comp:GetHudPosition()):thenReturn(10, 20)
	dnd:StartDrag(comp)

	assertTrue(dnd:IsDragging())
end

function testRegisterDropTarget()
	local parent = MoC:New()
	local dnd = DragDrop:New(parent)

	local target = {}
	local ok, actual = pcall(dnd.RegisterDropTarget, dnd, target)
	assertFalse(ok)
	assertEquals("No callback OnDrop defined for target", actual)

end
