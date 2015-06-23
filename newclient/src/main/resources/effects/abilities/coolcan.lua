local charge = function()
	
		
	local fieldr = CreateParticleEffect("coolcanopen")
	fieldr:atEntityMetaNode("SOURCE", "r_hand")
	fieldr:envelope(0, 2, 5)	
	
	local trigsound = CreateSoundEffect("trigfriendly")
	trigsound:playAtEntity("SOURCE")

	return fieldr, trigsound
end

local apply = function()

	local effect = CreateParticleEffect("coolcancloud")
	effect:atEntityMetaNode("TARGET", "head")
	effect:envelope(0, 4, 0)
	

	local sound = CreateSoundEffect("lilhealhit")
	sound:playAtEntity("TARGET")	
	
	
	return effect, sound
end

return {charge = charge, apply = apply}