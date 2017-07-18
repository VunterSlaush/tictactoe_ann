vacio   = [0; 0; 0; 0; 0; 0; 0; 0; 0];
es1     = [2; 0; 0; 0; 0; 0; 0; 0; 0];
es2     = [0; 0; 2; 0; 0; 0; 0; 0; 0];
centro  = [0; 0; 0; 0; 2; 0; 0; 0; 0];
es3     = [0; 0; 0; 0; 0; 0; 2; 0; 0];
es4     = [0; 0; 0; 0; 0; 0; 0; 0; 2];


l1     = [1; 0; 0;
          1; 0; 0;
          0; 0; 0];

l2     = [0; 1; 0;
          0; 1; 0;
          0; 0; 0];

l3     = [0; 0; 1;
          0; 0; 1;
          0; 0; 0];


l4     = [0; 0; 0;
          1; 0; 0;
          1; 0; 0];

l5     = [0; 0; 0;
          0; 1; 0;
          0; 1; 0];

l6     = [0; 0; 0;
          0; 0; 1;
          0; 0; 1];

v1     = [1; 0; 0;
          0; 1; 0;
          0; 0; 0];

v2     = [0; 0; 1;
          0; 1; 0;
          0; 0; 0];

v3     = [0; 0; 0;
          0; 1; 0;
          0; 0; 1];


v4     = [0; 0; 0;
          0; 1; 0;
          1; 0; 0];



e0 = [0; 0; 0; 0];
e1 = [0; 0; 0; 1];
e2 = [0; 0; 1; 0];
e3 = [0; 0; 1; 1];
e4 = [0; 1; 0; 0];
e5 = [0; 1; 0; 1];
e6 = [0; 1; 1; 0];
e7 = [0; 1; 1; 1];
e8 = [1; 0; 0; 0];


net = newff(minmax(vacio), [7 4], {'logsig','logsig'}, 'traingd');

net.trainParam.epochs = 1000;
net.trainParam.goal = 1e-8;

[net, tr] = train(net, vacio, e0);
[net, tr] = train(net, vacio, e2);
[net, tr] = train(net, vacio, e6);
[net, tr] = train(net, vacio, e8);
[net, tr] = train(net, vacio, e4);

[net, tr] = train(net, centro, e0);
[net, tr] = train(net, centro, e2);
[net, tr] = train(net, centro, e6);
[net, tr] = train(net, centro, e8);

[net, tr] = train(net, es1, e4);
[net, tr] = train(net, es2, e4);
[net, tr] = train(net, es3, e4);
[net, tr] = train(net, es4, e4);

[net, tr] = train(net, l4, e0);
[net, tr] = train(net, l5, e1);
[net, tr] = train(net, l6, e2);
[net, tr] = train(net, l1, e6);
[net, tr] = train(net, l2, e7);
[net, tr] = train(net, l3, e8);
