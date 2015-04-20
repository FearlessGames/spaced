local travel = function()
	local trail1 = CreateParticleEffect("overcharge_trail")
	trail1:projectile("SOURCE", "weapon", "TARGET", "impact")
	local core = CreateParticleEffect("overcharge_core")
	core:projectile("SOURCE", "weapon", "TARGET", "impact")
	local core1 = CreateParticleEffect("overcharge_core1")
	core1:projectile("SOURCE", "weapon", "TARGET", "impact")
	
	local sound = CreateSoundEffect("overchargeshoot")
	sound:fromTo("SOURCE", "TARGET")		
	
	return trail1, core, core1, sound
end
return {travel = travel}