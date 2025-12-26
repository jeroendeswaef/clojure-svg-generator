 linear_extrude(10)
 union() {
     translate([0, -60, 0])
     import("hinge.svg", center=false, $fn = 100); 
     
     translate([20, -20, 0])
     square([32, 20]);
     
     translate([-20, 20, 0])
     square([20, 32]);
 }