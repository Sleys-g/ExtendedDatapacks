package sleys.efedp.forge.system.animations.json.properties.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.forge.system.animations.json.properties.time.events.TaskableLaserShapeWorldDamageEvent;

import java.util.ArrayList;
import java.util.List;

public final class LaserShapeHelper {

    private LaserShapeHelper() {}

    public static List<Vec3> generate(TaskableLaserShapeWorldDamageEvent.LaserShape shape,
                                      Vec3 origin, double size, double spacing) {
        return switch (shape) {
            case LINE -> line(origin, size, spacing);
            case ARROW -> arrow(origin, size, spacing);
            case X -> x(origin, size, spacing);
            case CROSS -> cross(origin, size, spacing);
            case CIRCLE -> circle(origin, size, spacing);
            case RING -> ring(origin, size, spacing);
            case DISC -> disc(origin, size, spacing);
            case TRIANGLE -> triangle(origin, size, spacing);
            case SQUARE -> square(origin, size, spacing);
            case ARC -> arc(origin, size, spacing, 120.0D);
            case SEMICIRCLE -> arc(origin, size, spacing, 180.0D);
            case STAR -> star(origin, size, spacing, 5);
            case CONE -> cone(origin, size, spacing, 45.0D);
        };
    }

    public static List<Vec3> line(Vec3 origin, double length, double spacing) {
        List<Vec3> points = new ArrayList<>();
        addLine(points, origin.add(0.0, 0.0, -length / 2.0), origin.add(0.0, 0.0, length / 2.0), spacing);
        return points;
    }

    public static List<Vec3> arrow(Vec3 origin, double size, double spacing) {
        List<Vec3> points = new ArrayList<>();

        var half = size * 0.5;
        var headSize = size * 0.25;

        var tail = origin.add(0, 0, -half);
        var tip = origin.add(0, 0, half);

        addLine(points, tail, tip, spacing);

        var left = tip.add(-headSize, 0, -headSize);
        var right = tip.add(headSize, 0, -headSize);

        addLine(points, tip, left, spacing);
        addLine(points, tip, right, spacing);

        return points;
    }

    public static List<Vec3> x(Vec3 origin, double size, double spacing) {
        List<Vec3> points = new ArrayList<>();

        var half = size * 0.5;
        addLine(points, origin.add(-half, 0, -half), origin.add(half, 0, half), spacing);
        addLine(points, origin.add(-half, 0, half), origin.add(half, 0, -half), spacing);

        return points;
    }

    public static List<Vec3> cross(Vec3 origin, double size, double spacing) {
        List<Vec3> points = new ArrayList<>();

        var half = size * 0.5;

        addLine(points, origin.add(-half, 0, 0), origin.add(half, 0, 0), spacing);
        addLine(points, origin.add(0, 0, -half), origin.add(0, 0, half), spacing);

        return points;
    }

    public static List<Vec3> circle(Vec3 origin, double radius, double spacing) {
        List<Vec3> points = new ArrayList<>();

        var segments = Math.max(8, (int) Math.ceil(2.0 * Math.PI * radius / spacing));
        for (var i = 0; i < segments; i++) {
            var angle = 2.0 * Math.PI * i / segments;
            points.add(origin.add(Math.cos(angle) * radius, 0, Math.sin(angle) * radius));
        }

        return points;
    }

    public static List<Vec3> ring(Vec3 origin, double radius, double spacing) {
        return circle(origin, radius, spacing);
    }

    public static List<Vec3> disc(Vec3 origin, double radius, double spacing) {
        List<Vec3> points = new ArrayList<>();

        var step = Math.max(spacing, 0.1D);
        for (var x = -radius; x <= radius; x += step) {
            for (var z = -radius; z <= radius; z += step) {

                if (x * x + z * z <= radius * radius) {
                    points.add(
                            origin.add(x, 0, z)
                    );
                }
            }
        }

        return points;
    }

    public static List<Vec3> triangle(Vec3 origin, double size, double spacing) {
        List<Vec3> points = new ArrayList<>();

        var half = size * 0.5;

        var top = origin.add(0, 0, half);
        var left = origin.add(-half, 0, -half);
        var right = origin.add(half, 0, -half);

        addLine(points, top, left, spacing);
        addLine(points, left, right, spacing);
        addLine(points, right, top, spacing);

        return points;
    }

    public static List<Vec3> square(Vec3 origin, double size, double spacing) {
        return rectangle(origin, size, size, spacing);
    }

    public static List<Vec3> rectangle(Vec3 origin, double width, double length, double spacing) {
        List<Vec3> points = new ArrayList<>();

        var halfWidth = width * 0.5;
        var halfLength = length * 0.5;

        var a = origin.add(-halfWidth, 0, -halfLength);
        var b = origin.add(halfWidth, 0, -halfLength);
        var c = origin.add(halfWidth, 0, halfLength);
        var d = origin.add(-halfWidth, 0, halfLength);

        addLine(points, a, b, spacing);
        addLine(points, b, c, spacing);
        addLine(points, c, d, spacing);
        addLine(points, d, a, spacing);

        return points;
    }

    public static List<Vec3> arc(Vec3 origin, double radius, double spacing, double degrees) {
        List<Vec3> points = new ArrayList<>();

        var radians = Math.toRadians(degrees);
        var segments = Math.max(4, (int) Math.ceil(radius * radians / spacing));
        var start = -radians * 0.5;

        for (var i = 0; i <= segments; i++) {
            var angle = start + radians * i / segments;
            points.add(origin.add(Math.sin(angle) * radius, 0, Math.cos(angle) * radius));
        }

        return points;
    }

    public static List<Vec3> star(Vec3 origin, double radius, double spacing, int points) {
        List<Vec3> result = new ArrayList<>();

        var innerRadius = radius * 0.4;
        var vertices = points * 2;

        List<Vec3> verticesList = new ArrayList<>(
                vertices
        );

        for (var i = 0; i < vertices; i++) {
            var angle = -Math.PI / 2.0 + Math.PI * 2.0 * i / vertices;
            var currentRadius = i % 2 == 0 ?
                    radius :
                    innerRadius;

            verticesList.add(origin.add(Math.cos(angle) * currentRadius, 0, Math.sin(angle) * currentRadius));
        }

        for (var i = 0; i < vertices; i++) {
            var a = verticesList.get(i);
            var b = verticesList.get((i + 1) % vertices);

            addLine(
                    result,
                    a,
                    b,
                    spacing
            );
        }

        return result;
    }

    public static List<Vec3> cone(Vec3 origin, double length, double spacing, double angleDegrees) {
        List<Vec3> points = new ArrayList<>();

        var angle = Math.toRadians(angleDegrees * 0.5);
        var steps = Math.max(1, (int) Math.ceil(length / spacing));

        for (var i = 0; i <= steps; i++) {
            var distance = length * i / steps;
            var width = Math.tan(angle) * distance;
            var sideSteps = Math.max(1, (int) Math.ceil(width * 2.0 / spacing));

            for (var j = 0; j <= sideSteps; j++) {
                var lateral = - width + (width * 2.0 * j / sideSteps);
                points.add(origin.add(lateral, 0, distance));
            }
        }

        return points;
    }

    private static void addLine(List<Vec3> points, Vec3 start, Vec3 end, double spacing) {
        var distance = start.distanceTo(end);
        var steps = Math.max(1, (int) Math.ceil(distance / spacing));

        for (var i = 0; i <= steps; i++) {
            var progress = (double) i / steps;
            points.add(start.lerp(end, progress));
        }
    }

    public static List<Vec3> groundPoints(Level level, List<Vec3> points) {
        List<Vec3> grounded = new ArrayList<>(points.size());

        for (var point : points) {
            var pos = BlockPos.containing(point);
            var mutable = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());

            while (mutable.getY() > level.getMinBuildHeight() && level
                    .getBlockState(mutable)
                    .getCollisionShape(level, mutable)
                    .isEmpty()) mutable.move(Direction.DOWN);

            if (!level.getBlockState(mutable).getCollisionShape(level, mutable).isEmpty()) {
                var ground = new Vec3(point.x, mutable.getY() + 1.0D, point.z);
                grounded.add(ground);
            }
        }

        return grounded;
    }
}
