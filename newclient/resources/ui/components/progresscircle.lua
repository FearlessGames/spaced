require("ui/components/component")

do
	local CreateProgressCircle = CreateProgressCircle
	local GetProgressCircleProgress = GetProgressCircleProgress
	local SetProgressCircleProgress = SetProgressCircleProgress
	local SetProgressCircleText = SetProgressCircleText


	ProgressCircle = extend({}, Component)

	function ProgressCircle:New(parent, width, height)
		local this = Component:New(CreateProgressCircle(parent.base, width, height))
		extend(this, self)
		return this
	end

	function ProgressCircle:SetProgress(value)
		SetProgressCircleProgress(self.base, value)
	end

	function ProgressCircle:GetProgress()
		return GetProgressCircleProgress(self.base)
	end

	function ProgressCircle:SetText(text)
		SetProgressCircleText(self.base, text)
	end
end

CreateProgressCircle = nil
GetProgressCircleProgress = nil
SetProgressCircleProgress = nil
