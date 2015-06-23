local charge = function()


	local chargespin = CreateParticleEffect("volatile_combustion_chargespin")
	chargespin:atAttachmentPoint("SOURCE", "Bip01_Head")
	chargespin:envelope(0, 10, 2)	
	
	local sparks = CreateParticleEffect("volatile_combustion_sparks")
	sparks:atAttachmentPoint("SOURCE", "Bip01_Head")
	sparks:envelope(0, 10, 1)
	
	
	local sparksr = CreateParticleEffect("volatile_combustion_charge")
	sparksr:atAttachmentPoint("SOURCE", "Bip01_R_Hand")
	sparksr:envelope(0, 10, 1)	

	local sparksl = CreateParticleEffect("volatile_combustion_charge")
	sparksl:atAttachmentPoint("SOURCE", "Bip01_L_Hand")
	sparksl:envelope(0, 10, 1)	
	
--[[	local trigsound = CreateSoundEffect("trigaggressive")
	trigsound:playAtEntity("SOURCE")
	trigsound:envelope(0, 8, 0.1)	]]
	
	local sound = CreateSoundEffect("overchargecharge")
	sound:playAtEntity("SOURCE")	
	sound:envelope(0, 8, 0.1)

	local loop = CreateSoundEffect("overchargeloop")
	loop:playAtEntity("SOURCE")	
	loop:envelope(0, 8, 0.3)	
	
	local loopnoise = CreateSoundEffect("overchargeloopnoise")
	loopnoise:playAtEntity("SOURCE")	
	loopnoise:envelope(0, 8, 0.3)	
	
	return chargespin, sparks, sparksr, sparksl,  sound, loop, loopnoise -- trigsound
end

local apply = function()

	local burst = CreateParticleEffect("volatile_combustion-hit")
	burst:atEntityMetaNode("TARGET", "impact")
	local squash = CreateParticleEffect("volatile_combustion-squash")
	squash:atEntityMetaNode("TARGET", "impact")
	local shock = CreateParticleEffect("volatile_combustion-shock")
	shock:atEntityMetaNode("TARGET", "impact")
	local smoke = CreateParticleEffect("hitsmoke")
	smoke:atEntityMetaNode("TARGET", "impact")

	local sound = CreateSoundEffect("overchargehit")
	sound:playAtEntity("TARGET")	
	local soundlow = CreateSoundEffect("lowhit")
	soundlow:playAtEntity("TARGET")		
	
	return  burst, squash, shock, smoke, sound, soundlow
end

return {charge = charge, apply = apply}