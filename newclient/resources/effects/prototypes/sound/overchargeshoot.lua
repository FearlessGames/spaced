local effect = CreateSoundPrototype("/sound/sfx/Overcharge/OverchargeShoot1.wav")

effect:setFalloff(1.2)
effect:setGain(0.6 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(2.0)

return effect