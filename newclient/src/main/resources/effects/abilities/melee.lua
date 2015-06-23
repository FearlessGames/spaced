local charge = function()
	local effectR = CreateParticleEffect("zap_charge")
	effectR:atAttachmentPoint("SOURCE", "Bip01_R_Hand")
	effectR:envelope(0, 2, 2)

	local effectL = CreateParticleEffect("zap_charge")
	effectL:atAttachmentPoint("SOURCE", "Bip01_L_Hand")
	effectL:envelope(0, 2, 2)	
	

	local effect1 = CreateParticleEffect("melee_charge")
	effect1:atAttachmentPoint("SOURCE", "Bip01_R_Hand")
	effect1:envelope(0, 0.2, 0.51)
		
	local field1 = CreateParticleEffect("meleefield")
	field1:atAttachmentPoint("SOURCE", "Bip01_R_Hand")
	field1:envelope(0, 2, 2)
	
	local effect2 = CreateParticleEffect("melee_charge")
	effect2:atAttachmentPoint("SOURCE", "Bip01_L_Hand")
	effect2:envelope(0, 0.2, 0.51)
		
	local field2 = CreateParticleEffect("meleefield")
	field2:atAttachmentPoint("SOURCE", "Bip01_L_Hand")
	field2:envelope(0, 2, 2)	
	
	local sound = CreateSoundEffect("meleeswisch", "meleeswisch_2", "meleeswisch_3", "meleeswisch_4")
	sound:playAtEntity("SOURCE")
	sound:envelope(0, 3, 0.1)
	
	local soundhai = CreateSoundEffect("meleewhouu", "meleehai_1", "meleehai_2", "meleehai_3", "meleehai_4", "meleehai_5")
	soundhai:playAtEntity("SOURCE")
	
	return effectR, effectL, sound, effect1, field1, effect2, field2, soundhai
end

local apply = function()
	local burst = CreateParticleEffect("zap_hit")
	burst:atEntityMetaNode("TARGET", "impact")
	local squash = CreateParticleEffect("zap_splash")
	squash:atEntityMetaNode("TARGET", "impact")
	squash:envelope(0, 4, 2)
	local shock = CreateParticleEffect("zap_shock")
	shock:atEntityMetaNode("TARGET", "impact")
	shock:envelope(0, 4, 2)

	
	local sound = CreateSoundEffect("meleehit_1", "meleehit_2", "meleehit_3")
	sound:playAtEntity("TARGET")
	local soundouch = CreateSoundEffect("meleeouch_1", "meleeouch_2", "meleeouch_3")
	soundouch:playAtEntity("TARGET")	
	local soundwhouu = CreateSoundEffect("meleewhouu")
	soundwhouu:playAtEntity("TARGET")		
	
	return burst, squash, shock, sound, soundouch
end

return {charge = charge, apply = apply}