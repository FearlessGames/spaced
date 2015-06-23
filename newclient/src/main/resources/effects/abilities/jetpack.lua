local charge = function()

	local soundgaslooptone = CreateSoundEffect("jetpackgaslooptone")
	soundgaslooptone:playAtEntity("SOURCE")
	soundgaslooptone:envelope(0,5000,1)
	
	local soundbaselooptone = CreateSoundEffect("jetpackbaselooptone")
	soundbaselooptone:playAtEntity("SOURCE")
	soundbaselooptone:envelope(0,5000,1)	
	
	local soundbasestart = CreateSoundEffect("jetpackgasstart")
	soundbasestart:playAtEntity("SOURCE")
	soundbasestart:envelope(0,5000,4)	

	local soundpump = CreateSoundEffect("jetpackgasloop")
  	soundpump:playAtEntity("SOURCE")
  	soundpump:envelope(0,5000,1)

	local jet1 = CreateParticleEffect("smoketrail_gas")
	jet1:atEntityMetaNode("SOURCE", "jet_low_left")
	jet1:envelope(0, 5000, 5)

	local jet2 = CreateParticleEffect("smoketrail_gas")
	jet2:atEntityMetaNode("SOURCE", "jet_low_right")
	jet2:envelope(0, 5000, 5)

	local jet3 = CreateParticleEffect("smoketrail_gas")
	jet3:atEntityMetaNode("SOURCE", "jet_high_left")
	jet3:envelope(0, 5000, 5)

	local jet4 = CreateParticleEffect("smoketrail_gas")
	jet4:atEntityMetaNode("SOURCE", "jet_high_right")
	jet4:envelope(0, 5000, 5)	
	
	local jet1_2 = CreateParticleEffect("jetflame_gas")
	jet1_2:atEntityMetaNode("SOURCE", "jet_low_left")
	jet1_2:envelope(0, 5000, 2)

	local jet2_2 = CreateParticleEffect("jetflame_gas")
	jet2_2:atEntityMetaNode("SOURCE", "jet_low_right")
	jet2_2:envelope(0, 5000, 2)

	local jet3_2 = CreateParticleEffect("jetflame_gas")
	jet3_2:atEntityMetaNode("SOURCE", "jet_high_left")
	jet3_2:envelope(0, 5000, 2)

	local jet4_2 = CreateParticleEffect("jetflame_gas")
	jet4_2:atEntityMetaNode("SOURCE", "jet_high_right")
	jet4_2:envelope(0, 5000, 2)		
	
	local jet1_3 = CreateParticleEffect("jetcore_gas")
	jet1_3:atEntityMetaNode("SOURCE", "jet_low_left")
	jet1_3:envelope(0, 5000, 2)

	local jet2_3 = CreateParticleEffect("jetcore_gas")
	jet2_3:atEntityMetaNode("SOURCE", "jet_low_right")
	jet2_3:envelope(0, 5000, 2)

	local jet3_3 = CreateParticleEffect("jetcore_gas")
	jet3_3:atEntityMetaNode("SOURCE", "jet_high_left")
	jet3_3:envelope(0, 5000, 2)

	local jet4_3 = CreateParticleEffect("jetcore_gas")
	jet4_3:atEntityMetaNode("SOURCE", "jet_high_right")
	jet4_3:envelope(0, 5000, 2)			
	
	return soundbasestart, soundbaselooptone, soundgaslooptone, soundpump, jet1, jet2, jet3, jet4,  jet1_2, jet2_2, jet3_2, jet4_2,  jet1_3, jet2_3, jet3_3, jet4_3
end

local travel = function()

	local soundgasstart = CreateSoundEffect("jetpackbasestart")
	soundgasstart:playAtEntity("SOURCE")
	soundgasstart:envelope(0,5000,4)	

	local soundbaselooptone = CreateSoundEffect("jetpackgaslooptone")
	soundbaselooptone:playAtEntity("SOURCE")
	soundbaselooptone:envelope(0,5000,1)

   local soundpump = CreateSoundEffect("jetpackbaseloop")
  	soundpump:playAtEntity("SOURCE")
  	soundpump:envelope(0,5000,1)

	local jet1 = CreateParticleEffect("smoketrail")
	jet1:atEntityMetaNode("SOURCE", "jet_low_left")
	jet1:envelope(0, 5000, 5)

	local jet2 = CreateParticleEffect("smoketrail")
	jet2:atEntityMetaNode("SOURCE", "jet_low_right")
	jet2:envelope(0, 5000, 5)

	local jet3 = CreateParticleEffect("smoketrail")
	jet3:atEntityMetaNode("SOURCE", "jet_high_left")
	jet3:envelope(0, 5000, 5)

	local jet4 = CreateParticleEffect("smoketrail")
	jet4:atEntityMetaNode("SOURCE", "jet_high_right")
	jet4:envelope(0, 5000, 5)	
	
	local jet1_2 = CreateParticleEffect("jetflame")
	jet1_2:atEntityMetaNode("SOURCE", "jet_low_left")
	jet1_2:envelope(0, 5000, 2)

	local jet2_2 = CreateParticleEffect("jetflame")
	jet2_2:atEntityMetaNode("SOURCE", "jet_low_right")
	jet2_2:envelope(0, 5000, 2)

	local jet3_2 = CreateParticleEffect("jetflame")
	jet3_2:atEntityMetaNode("SOURCE", "jet_high_left")
	jet3_2:envelope(0, 5000, 2)

	local jet4_2 = CreateParticleEffect("jetflame")
	jet4_2:atEntityMetaNode("SOURCE", "jet_high_right")
	jet4_2:envelope(0, 5000, 2)		
	
	local jet1_3 = CreateParticleEffect("jetcore")
	jet1_3:atEntityMetaNode("SOURCE", "jet_low_left")
	jet1_3:envelope(0, 5000, 2)

	local jet2_3 = CreateParticleEffect("jetcore")
	jet2_3:atEntityMetaNode("SOURCE", "jet_low_right")
	jet2_3:envelope(0, 5000, 2)

	local jet3_3 = CreateParticleEffect("jetcore")
	jet3_3:atEntityMetaNode("SOURCE", "jet_high_left")
	jet3_3:envelope(0, 5000, 2)

	local jet4_3 = CreateParticleEffect("jetcore")
	jet4_3:atEntityMetaNode("SOURCE", "jet_high_right")
	jet4_3:envelope(0, 5000, 2)			
	
	return soundgasstart, soundbaselooptone, soundpump, jet1, jet2, jet3, jet4,  jet1_2, jet2_2, jet3_2, jet4_2,  jet1_3, jet2_3, jet3_3, jet4_3
end

local hit = function()

end

return {charge = charge, travel = travel, hit = hit}