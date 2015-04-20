local fontEffectFactory = GetFontEffectFactory()

local arial = CreateFontFamily("fonts/arial.ttf", "arial", false, false)
local arialOutlined = CreateFontFamilyWithEffects("fonts/arial.ttf", "arialOutlined", false, false, fontEffectFactory:CreateOutline(0, 0, 0, 2), fontEffectFactory:CreateColorEffect(1, 1, 1))
local consolas = CreateFontFamily("fonts/consola.ttf","consola", false, false)
local eras = CreateFontFamily("fonts/ERASMD.TTF","eras", false, false)
local comic = CreateFontFamily("fonts/comic.ttf", "comic",false, false)
local verdana = CreateFontFamily("fonts/verdana.ttf","verdana", false, false)

local fonts = {arial = arial, arialOutlined = arialOutlined, consolas = consolas, eras = eras, comic = comic, verdana = verdana}

function GetFont(name)
	return fonts[name]
end

function GetHeadline1()
	return GetFont("comic")
end

function GetHeadline2()
	return GetFont("comic")
end

function GetBodyFont()
	return GetFont("arial")
end

function GetInputFont()
	return GetFont("consolas")
end

function GetCutsceneFont()
	return 20, GetFont("comic")
end