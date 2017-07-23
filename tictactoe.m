vacio   = [0; 0; 0; 0; 0; 0; 0; 0; 0];
es1     = [2; 0; 0; 0; 0; 0; 0; 0; 0];
es2     = [0; 0; 2; 0; 0; 0; 0; 0; 0];
centro  = [0; 0; 0; 0; 2; 0; 0; 0; 0];
es3     = [0; 0; 0; 0; 0; 0; 2; 0; 0];
es4     = [0; 0; 0; 0; 0; 0; 0; 0; 2];





e0 = [0; 0; 0; 0];
e1 = [0; 0; 0; 1];
e2 = [0; 0; 1; 0];
e3 = [0; 0; 1; 1];
e4 = [0; 1; 0; 0];
e5 = [0; 1; 0; 1];
e6 = [0; 1; 1; 0];
e7 = [0; 1; 1; 1];
e8 = [1; 0; 0; 0];


net = newff(minmax(vacio), [10 4], {'logsig','logsig'}, 'traingd');

net.trainParam.epochs = 500;
net.trainParam.goal = 1e-8;

[net, tr] = train(net, vacio, e0);

[net, tr] = train(net, centro, e0);

[net, tr] = train(net, es1, e4);
[net, tr] = train(net, es2, e4);
[net, tr] = train(net, es3, e4);
[net, tr] = train(net, es4, e4);
