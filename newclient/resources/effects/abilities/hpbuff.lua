local charge = function()

	local sparks = CreateParticleEffect("hpbuff_sparks")
	sparks:atAttachmentPoint("TARGET", "Bip01_Spine1")
	sparks:envelope(0, 40000, 2)
	
	local field = CreateParticleEffect("hpbuff_field")
	field:atAttachmentPoint("TARGET", "Bip01_Spine1")
	field:envelope(0, 40000, 2)	

	local sound = CreateSoundEffect("hpbuffloop")
	sound:playAtEntity("TARGET")
	sound:envelope(0, 40000, 1)	
	

	return sparks, sound, field
end


return {charge = charge}