require("lua/core/inheritance")

ChatModel = {}

function ChatModel:New(visibleLines, linesInPageJump)
	local this = {}
	extend(this, self)
	this.visibleLines = visibleLines
	this.linesInPageJump = linesInPageJump
	this.chatLines = {}
	this.currentLine = 0
	this.listeners = {}
	return this
end

function ChatModel:AddListener(listener)
	table.insert(self.listeners, listener)
end

function ChatModel:UpdateChat()
	local text  = ""
	local startIndex = math.max(1, self.currentLine - self.visibleLines + 1)
	--	print(startIndex, self.currentLine, self.visibleLines)
	for i = startIndex, self.currentLine - 1, 1 do
		text = text .. self.chatLines[i] .. "\n"
	end
	if(self.currentLine == #self.chatLines) then
		text = text .. self.chatLines[self.currentLine]
	else
		text = text .. "          --- more ---"
	end
	self.text = text
	for _, listener in pairs(self.listeners) do
		listener:OnUpdate(self)
	end
end

function ChatModel:GetViewableText()
	return self.text
end



function ChatModel:PageUpKeyDown()
	local currentBefore = self.currentLine
	self.currentLine = math.max(math.min(self.visibleLines, #self.chatLines), self.currentLine - self.linesInPageJump)
	if(currentBefore ~= self.currentLine) then
		self:UpdateChat()
	end
end

function ChatModel:PageDownKeyDown()
	local currentBefore = self.currentLine
	self.currentLine = math.min(#self.chatLines, self.currentLine + self.linesInPageJump)
	if(currentBefore ~= self.currentLine) then
		self:UpdateChat()
	end
end

function ChatModel:EndKeyDown()
	local currentBefore = self.currentLine
	self.currentLine = #self.chatLines
	if(currentBefore ~= self.currentLine) then
		self:UpdateChat()
	end
end

function ChatModel:AddLine(message)
	local newLine = #self.chatLines + 1
	self.chatLines[newLine] = message
	if(self.currentLine == #self.chatLines - 1) then
		self.currentLine = self.currentLine + 1
	end
	self:UpdateChat()
end

function ChatModel:HasHistory()
	return self.currentLine - self.visibleLines > 0
end

function ChatModel:IsAtTail()
	return #self.chatLines == self.currentLine
end
