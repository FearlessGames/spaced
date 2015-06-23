local charge = function()

	
	local effectl = CreateParticleEffect("lilheal_charge")
	effectl:atAttachmentPoint("SOURCE", "Bip01_R_Hand")
	effectl:envelope(0, 2, 5)
		
	local fieldl = CreateParticleEffect("healfield")
	fieldl:atAttachmentPoint("SOURCE", "Bip01_R_Hand")
	fieldl:envelope(0, 2, 5)
	
	local effectr = CreateParticleEffect("lilheal_charge")
	effectr:atAttachmentPoint("SOURCE", "Bip01_L_Hand")
	effectr:envelope(0, 2, 5)
		
	local fieldr = CreateParticleEffect("healfield")
	fieldr:atAttachmentPoint("SOURCE", "Bip01_L_Hand")
	fieldr:envelope(0, 2, 5)	
	
	local trigsound = CreateSoundEffect("trigfriendly")
	trigsound:playAtEntity("SOURCE")
	
	local sound = CreateSoundEffect("lilhealcharge")
	sound:playAtEntity("SOURCE")
	
	local soundlow = CreateSoundEffect("lilhealchargelow")
	soundlow:playAtEntity("SOURCE")	

	local healinc = CreateParticleEffect("healinc")
	healinc:atEntityMetaNode("TARGET", "head")
	healinc:envelope(0, 2, 0)		
	
	return effectl, fieldl, effectr, fieldr, sound, trigsound, soundlow, healinc
end

--local travel = function()
--	local effect = CreateParticleEffect("lilheal")
--	effect:fromTo("SOURCE", "TARGET")
--	return effect
--end

local apply = function()

	local effect = CreateParticleEffect("lilheal")
	effect:atEntityMetaNode("TARGET", "body")
	effect:envelope(0, 2, 0)
	
	local hit = CreateParticleEffect("healhitfield")
	hit:atEntityMetaNode("TARGET", "body")
	
	local sound = CreateSoundEffect("lilhealhit")
	sound:playAtEntity("TARGET")	
	
	local soundlow = CreateSoundEffect("lilhealhitlow")
	soundlow:playAtEntity("TARGET")	
	
	return effect, hit, sound, soundlow
end

return {charge = charge, apply = apply}