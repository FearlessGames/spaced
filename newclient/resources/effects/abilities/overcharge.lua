local charge = function()
	local charge = CreateParticleEffect("overcharge_charge")
	charge:atAttachmentPoint("SOURCE", "Bip01_Head")
	charge:envelope(0, 7, 1)

	local chargespin = CreateParticleEffect("overcharge_chargespin")
	chargespin:atAttachmentPoint("SOURCE", "Bip01_Head")
	chargespin:envelope(0, 7, 1)	
	
	local sparks = CreateParticleEffect("overcharge_sparks")
	sparks:atAttachmentPoint("SOURCE", "Bip01_Head")
	sparks:envelope(0, 7, 1)
	
	
	local sparksr = CreateParticleEffect("overcharge_charge")
	sparksr:atAttachmentPoint("SOURCE", "Bip01_R_Hand")
	sparksr:envelope(0, 7, 1)	

	local sparksl = CreateParticleEffect("overcharge_charge")
	sparksl:atAttachmentPoint("SOURCE", "Bip01_L_Hand")
	sparksl:envelope(0, 7, 1)		
	
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
	
	return charge, chargespin, sparks, sparksr, sparksl, sound, loop, loopnoise -- trigsound
end
local apply = function()
	local shootspin = CreateParticleEffect("overcharge_shootspin")
	shootspin:atEntityMetaNode("SOURCE", "head")
	local shootsound = CreateSoundEffect("overchargeshoot")
	shootsound:playAtEntity("TARGET")		


	local burst = CreateParticleEffect("overcharge-hit")
	burst:atEntityMetaNode("TARGET", "impact")
	local squash = CreateParticleEffect("overcharge-squash")
	squash:atEntityMetaNode("TARGET", "impact")
	local shock = CreateParticleEffect("overcharge-shock")
	shock:atEntityMetaNode("TARGET", "impact")
	local smoke = CreateParticleEffect("hitsmoke")
	smoke:atEntityMetaNode("TARGET", "impact")

	local sound = CreateSoundEffect("overchargehit")
	sound:playAtEntity("TARGET")	
	local soundlow = CreateSoundEffect("lowhit")
	soundlow:playAtEntity("TARGET")		
	
	return shootspin, shootsound, burst, squash, shock, smoke, sound, soundlow
end

return {charge = charge, apply = apply}