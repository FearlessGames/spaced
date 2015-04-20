local charge = function()
	
	local effectR = CreateParticleEffect("lazorcharge")
	effectR:atAttachmentPoint("SOURCE", "Bip01_R_Hand")
	effectR:envelope(0, 0.6, 1)

	local effectL = CreateParticleEffect("lazorcharge")
	effectL:atAttachmentPoint("SOURCE", "Bip01_L_Hand")
	effectL:envelope(0, 0.6, 1)		


	local footR = CreateParticleEffect("lazorcharge")
	footR:atAttachmentPoint("SOURCE", "Bip01_R_Foot")
	footR:envelope(0, 0.6, 1)

	local footL = CreateParticleEffect("lazorcharge")
	footL:atAttachmentPoint("SOURCE", "Bip01_L_Foot")
	footL:envelope(0, 0.6, 1)
	
	local trigsound = CreateSoundEffect("trigaggressive")
	trigsound:playAtEntity("SOURCE")
	trigsound:envelope(0, 3, 0.1)	
	
	local sound = CreateSoundEffect("lazorcharge")
	sound:playAtEntity("SOURCE")
	sound:envelope(0, 3, 0.1)
	local soundlow = CreateSoundEffect("lazorchargelow")
	soundlow:playAtEntity("SOURCE")
	soundlow:envelope(0, 3, 0.1)
	return effectR, effectL, footL, footR, sound, soundlow, trigsound
end

local apply = function()
	local burst = CreateParticleEffect("lazor_blast-hit")
	burst:atEntityMetaNode("TARGET", "impact")
	local squash = CreateParticleEffect("lazor_blast-squash")
	squash:atEntityMetaNode("TARGET", "impact")
	squash:envelope(0, 4, 2)
	local shock = CreateParticleEffect("lazor_blast-shock")
	shock:atEntityMetaNode("TARGET", "impact")
	shock:envelope(0, 4, 2)
	local smoke = CreateParticleEffect("lazorsmoke")
	smoke:atEntityMetaNode("TARGET", "impact")
	smoke:envelope(0, 4, 5)
	
	local sound = CreateSoundEffect("lazorhit", "lazorhit_2", "lazorhit_3")
	sound:playAtEntity("TARGET")
	local soundlow = CreateSoundEffect("lazorhitlow", "lazorhitlow_2", "lazorhitlow_3")
	soundlow:playAtEntity("TARGET")
	soundlow:envelope(0, 3, 4)		
	
	

	return burst, squash, shock, smoke, sound, soundlow
end

return {charge = charge, apply = apply}