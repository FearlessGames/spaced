local effect = CreateSoundPrototype("/sound/sfx/Lilheal/LilhealHit1.wav")

effect:setFalloff(0.7)
effect:setGain(0.3 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(6.0)

return effect