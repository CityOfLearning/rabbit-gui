Entity Component
====================

An entity component allows living entities in the game to be rendered to a GUI. You may have to experiment for handling the scale of the entities due to the fact that some entities are very small and others are quite large. Entities can be rotated if enabled by dragging the mouse over the entity.

Display Entity and Display Heads
--------------------------------

Complmenting the entity component is a generic bipedal player like character whose texture can by changed dynamically. The display entity supports both old and new skin formats so if a skin texture has not been updated that is not a problem as it will still render correctly.

Display heads are similar in that their texture can be dynamic but only the head will be rendered. This could be useful for RPG like game elements where speech is a component of a GUI.

### Constructors

```
EntityComponent(int x, int y, int width, int height, EntityLivingBase entity)
EntityComponent(int x, int y, int width, int height, EntityLivingBase entity, int rotation)
EntityComponent(int x, int y, int width, int height, EntityLivingBase entity, int rotation, float zoom)
EntityComponent(int x, int y, int width, int height, EntityLivingBase entity, int rotation, float zoom, boolean canRotate)

DisplayEntity(World worldIn)
DisplayEntityHead(World worldIn)
```