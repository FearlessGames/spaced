local travel = function()
	local trail1 = CreateParticleEffect("lazor_blast")
	trail1:projectile("SOURCE", "weapon", "TARGET", "impact")
	trail1:envelope(0, 3, 3)
	local core = CreateParticleEffect("lazor_blast_core")
	core:projectile("SOURCE", "weapon", "TARGET", "impact")
	core:envelope(0, 3, 3)
	local core1 = CreateParticleEffect("lazor_blast_core1")
	core1:projectile("SOURCE", "weapon", "TARGET", "impact")
	core1:envelope(0, 3, 3)
	
	local sound = CreateSoundEffect("lazorbeam_1", "lazorbeam_2", "lazorbeam_3", "lazorbeam_4", "lazorbeam_5")
	sound:fromTo("SOURCE", "TARGET")
	sound:envelope(0, 3, 3.0)	
	
	local sound2 = CreateSoundEffect("lazorbeam_1", "lazorbeam_2", "lazorbeam_3", "lazorbeam_4", "lazorbeam_5")
	sound2:playAtEntity("SOURCE")	
	sound2:envelope(0, 3, 3.0)		
	
	local shootsound = CreateSoundEffect("lazorshoot", "lazorshoot_2", "lazorshoot_3", "lazorshoot_4")
	shootsound:playAtEntity("SOURCE")	

	return trail1, core, core1, sound ,sound2, shootsound
end
return {travel = travel}