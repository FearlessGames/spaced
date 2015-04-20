local charge = function()
	local shine = CreateParticleEffect("plasmacharge")
	shine:playAtEntity("SOURCE")
	local field = CreateParticleEffect("plasmachargefield")
	field:playAtEntity("SOURCE")
	
	local trigsound = CreateSoundEffect("trigaggressive")
	trigsound:playAtEntity("SOURCE")
	
	local sound = CreateSoundEffect("plasmaballcharge")
	sound:playAtEntity("SOURCE")	
	
	return shine, field ,trigsound ,sound
end

local travel = function()
	local fieldball = CreateParticleEffect("plasma")
	fieldball:fromTo("SOURCE", "TARGET")
	fieldball:envelope(0, 5, 0)
	local core = CreateParticleEffect("plasmacore")
	core:fromTo("SOURCE", "TARGET")
	core:envelope(0, 5, 0)
	local smoke = CreateParticleEffect("smoketrail")
	smoke:fromTo("SOURCE", "TARGET")
	
	local smoke2 = CreateParticleEffect("smoketrail2")
	smoke2:fromTo("SOURCE", "TARGET")
	
	local sound = CreateSoundEffect("plasmaballshoot")
	sound:playAtEntity("SOURCE")		
		
	return fieldball, core, smoke, sound, smoke2
end

local apply = function()
	local squash = CreateParticleEffect("plasma_hit")
	squash:playAtEntity("TARGET")
	local splash = CreateParticleEffect("plasma_hitsplash")
	splash:playAtEntity("TARGET")
	
	local sound = CreateSoundEffect("plasmaballhit")
	sound:playAtEntity("TARGET")		
	
	return squash, splash, sound
	

end

return {charge = charge, travel = travel, apply = apply}