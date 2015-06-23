local effect = CreateSoundPrototype("/sound/sfx/Melee/whouu.wav")

effect:setFalloff(1.3)
effect:setGain(0.12)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(1.6)

return effect