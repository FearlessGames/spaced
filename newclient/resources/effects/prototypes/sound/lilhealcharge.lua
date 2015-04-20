local effect = CreateSoundPrototype("/sound/sfx/Lilheal/LilhealCharge1.wav")

effect:setFalloff(1.0)
effect:setGain(0.1 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(3.0)

return effect