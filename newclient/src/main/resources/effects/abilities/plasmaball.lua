local charge = function()
	local shine = CreateParticleEffect("plasmacharge")
	shine:playAtEntity("SOURCE")
	local field = CreateParticleEffect("plasmachargefield")
	field:playAtEntity("SOURCE")
	
	local trigsound = CreateSoundEffect("trigaggressive")
	trigsound:playAtEntity("SOURCE")
	
	local sound = CreateSoundEffect("plasmaballcharge")
	sound:playAtEntity("SOURCE")	
	
	local soundpump = CreateSoundEffect("plasmaballchargepump")
	soundpump:playAtEntity("SOURCE")		
	
	
	return shine, field, trigsound, sound, soundpump
end

local apply = function()
	local squash = CreateParticleEffect("plasma_hit")
	squash:atEntityMetaNode("TARGET", "impact")
	local splash = CreateParticleEffect("plasma_hitsplash")
	splash:atEntityMetaNode("TARGET", "impact")
	
	local sound = CreateSoundEffect("plasmaballhit")
	sound:playAtEntity("TARGET")	

	local soundlow = CreateSoundEffect("lowhit")
	soundlow:playAtEntity("TARGET")	
	
	return squash, splash, sound, soundlow
	

end

return {charge = charge, apply = apply}