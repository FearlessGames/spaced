local effect = CreateSoundPrototype("/sound/sfx/Lilheal/HitLow1.wav")

effect:setFalloff(0.7)
effect:setGain(0.1 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(8.0)

return effect