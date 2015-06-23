local travel = function()
	local fieldball = CreateParticleEffect("plasma")
	fieldball:projectile("SOURCE", "weapon", "TARGET", "impact")
	fieldball:envelope(0, 5, 0)

	local core = CreateParticleEffect("plasmacore")
	core:projectile("SOURCE", "weapon", "TARGET", "impact")
	core:envelope(0, 5, 0)

	local smoke = CreateParticleEffect("smoketrail")
	smoke:projectile("SOURCE", "weapon", "TARGET", "impact")
	
	local smoke2 = CreateParticleEffect("smoketrail2")
	smoke2:projectile("SOURCE", "weapon", "TARGET", "impact")
	
	local sound = CreateSoundEffect("plasmaballshoot")
	sound:fromTo("SOURCE", "TARGET")
	sound:envelope(0, 10, 0.1)	
	
	local soundpump = CreateSoundEffect("plasmaballchargepump")
	soundpump:fromTo("SOURCE", "TARGET")		
		
	return fieldball, core, smoke, sound, smoke2, soundpump
end
return {travel = travel}