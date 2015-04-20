local travel = function()

	local shootsound = CreateSoundEffect("overchargeshoot")
	shootsound:playAtEntity("SOURCE")	

	local shootspin = CreateParticleEffect("volatile_combustion_shootspin")
	shootspin:atEntityMetaNode("SOURCE", "head")
	local trail1 = CreateParticleEffect("volatile_combustion_trail")
	trail1:projectile("SOURCE", "head", "TARGET", "impact")
	local core = CreateParticleEffect("volatile_combustion_trail")
	core:projectile("SOURCE", "l_hand", "TARGET", "impact")
	local core1 = CreateParticleEffect("volatile_combustion_trail")
	core1:projectile("SOURCE", "r_hand", "TARGET", "impact")
	
	local sound = CreateSoundEffect("overchargeshoot")
	sound:fromTo("SOURCE", "TARGET")		
	
	return  shootsound, trail1, core, core1, sound, shootspin
end
return {travel = travel}