local effect = CreateSoundPrototype("/sound/sfx/Overcharge/OverchargeHitLow1.wav")

effect:setFalloff(0.6)
effect:setGain(1.5 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(3.0)

return effect