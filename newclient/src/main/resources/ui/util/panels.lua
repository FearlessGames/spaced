require("ui/components/borderpanel")
require("ui/components/filledpanel")

function createPanel(useBorder, parent, params)
	local borderClass
	if useBorder then
		borderClass = BorderPanel
	else
		borderClass = FilledPanel
	end
	return borderClass.New(borderClass, parent, params)
end