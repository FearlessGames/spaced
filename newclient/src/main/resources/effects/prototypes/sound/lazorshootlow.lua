local effect = CreateSoundPrototype("/sound/sfx/Lazor/ShootLow1.wav")

effect:setFalloff(0.6)
effect:setGain(0.1 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(2.0)

return effect