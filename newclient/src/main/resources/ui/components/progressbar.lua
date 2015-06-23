require("ui/components/component")

do
	local CreateProgressBar = CreateProgressBar
	local GetProgressBarProgress = GetProgressBarProgress
	local SetProgressBarProgress = SetProgressBarProgress
	local SetProgressBarHorizontal = SetProgressBarHorizontal
	local SetProgressBarBackgroundAlpha = SetProgressBarBackgroundAlpha
	local GetProgressBarLabel = GetProgressBarLabel


	TextMode = {NONE = {}, ABSOLUTE = {}, RELATIVE = {}}
	ProgressBar = extend({}, Component)

	function ProgressBar:New(parent, width, height, r, g, b, a)
		local this = Component:New(CreateProgressBar(parent.base, width, height, r, g, b, a or 1))
		extend(this, self)
		this.textMode = TextMode.NONE
		this.barLabel = Label:NewRaw(GetProgressBarLabel(this.base))
		return this
	end

	function ProgressBar:SetTextMode(mode)
		self.textMode = mode
	end

	function ProgressBar:SetVertical()
		SetProgressBarHorizontal(self.base, false)
	end

	function ProgressBar:SetBackgroundAlpha(alpha)
		SetProgressBarBackgroundAlpha(self.base, alpha)
	end

	function ProgressBar:GetProgress()
		return GetProgressBarProgress(self.base)
	end

	function ProgressBar:SetFont(size, font)
		self.barLabel:SetFont(size, font)
	end

	function ProgressBar:Update(current, max, percentage)
		local text = ""
		local progress = 0
		if current and max then
			progress = percentage
			if(self.textMode == TextMode.ABSOLUTE) then
				text = current .. "/" .. max
			elseif(self.textMode == TextMode.RELATIVE) then
				text = string.format("%.0d%%", percentage * 100)
			end
		end
		self.barLabel:SetText(text)
		SetProgressBarProgress(self.base, progress)
	end
end

CreateProgressBar = nil
GetProgressBarProgress = nil
SetProgressBarProgress = nil
SetProgressBarHorizontal = nil
SetProgressBarBackgroundAlpha = nil
GetProgressBarLabel = nil