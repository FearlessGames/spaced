local effect = CreateSoundPrototype("/sound/sfx/Overcharge/OverchargeHit1.wav")

effect:setFalloff(1.5)
effect:setGain(0.5 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(2.0)

return effect