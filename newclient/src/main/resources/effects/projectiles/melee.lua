local travel = function()

	local core1 = CreateParticleEffect("zap_trail")
	core1:projectile("SOURCE", "weapon", "TARGET", "impact")
	core1:envelope(0, 3, 3)

	return core1
end
return {travel = travel}