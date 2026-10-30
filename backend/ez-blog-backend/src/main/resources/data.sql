-- Sample data for ezBlog Platform Database
-- This file is automatically executed after schema.sql

-- Insert sample users
-- Sample data for ezBlog (DEVELOPMENT ONLY). Every user's password is: Password123!
INSERT INTO users (name, email, password, bio, created_at, updated_at) VALUES
                                                                           ('Vikas Seervi',  'vikas@example.com', '$2a$10$PZ54NMRlQFtNFyhk7Ohf/uvXHTzN9QQx3ZbkWjCm57xGl./sZVpgS', 'Software developer and tech enthusiast', now(), now()),
                                                                           ('John Doe',      'john@example.com',  '$2a$10$PZ54NMRlQFtNFyhk7Ohf/uvXHTzN9QQx3ZbkWjCm57xGl./sZVpgS', 'Software developer and tech enthusiast', now(), now()),
                                                                           ('Jane Smith',    'jane@example.com',  '$2a$10$PZ54NMRlQFtNFyhk7Ohf/uvXHTzN9QQx3ZbkWjCm57xGl./sZVpgS', 'Content creator and lifestyle blogger', now(), now()),
                                                                           ('Sarah Johnson', 'sarah@example.com', '$2a$10$PZ54NMRlQFtNFyhk7Ohf/uvXHTzN9QQx3ZbkWjCm57xGl./sZVpgS', 'Travel blogger and photographer', now(), now()),
                                                                           ('Mike Chen',     'mike@example.com',  '$2a$10$PZ54NMRlQFtNFyhk7Ohf/uvXHTzN9QQx3ZbkWjCm57xGl./sZVpgS', 'Food critic and chef', now(), now()),
                                                                           ('Emma Wilson',   'emma@example.com',  '$2a$10$PZ54NMRlQFtNFyhk7Ohf/uvXHTzN9QQx3ZbkWjCm57xGl./sZVpgS', 'Fitness trainer and health coach', now(), now());

-- Insert sample posts with rich content
INSERT INTO posts (title, content, excerpt, author_id, category, read_time, published, created_at, updated_at) VALUES
                                                                                                                   ('Getting Started with React Hooks', 'React Hooks have revolutionized the way we write React components. In this comprehensive guide, we''ll explore the most commonly used hooks and how they can simplify your code.

## useState Hook

The useState hook is the most fundamental hook in React. It allows you to add state to functional components. Here''s a simple example:

```
const [count, setCount] = useState(0);
```

## useEffect Hook

The useEffect hook lets you perform side effects in functional components. It serves the same purpose as componentDidMount, componentDidUpdate, and componentWillUnmount combined.

## Custom Hooks

One of the best features of hooks is the ability to create custom hooks. This allows you to extract component logic into reusable functions.

### Best Practices

1. Only call hooks at the top level
2. Only call hooks from React functions
3. Name custom hooks with "use" prefix

Hooks have made React code more readable and maintainable. Give them a try in your next project!', 'Discover how React Hooks revolutionize component development, simplifying state management and lifecycle methods.', 1, 'Technology', 8, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                   ('The Future of Web Development', 'As we move through 2025, the web development landscape continues to evolve at a rapid pace. Let''s explore the key trends shaping the future of our industry.

## AI-Powered Development

Artificial Intelligence is no longer just a buzzword. AI-powered tools are becoming essential in every developer''s toolkit, from code completion to automated testing.

## WebAssembly Goes Mainstream

WebAssembly has finally reached mainstream adoption. More and more applications are using WASM to achieve near-native performance in the browser.

## The Rise of Edge Computing

Edge computing is changing how we think about application architecture. By processing data closer to the user, we can achieve unprecedented performance improvements.

## Serverless Evolution

Serverless architecture has matured significantly. The tooling has improved, cold starts are less of an issue, and more companies are adopting serverless-first strategies.

## Environmental Consciousness

Green web development is becoming a priority. Developers are now considering the carbon footprint of their applications and optimizing for energy efficiency.

The future is exciting, and there''s never been a better time to be a web developer!', 'Explore emerging trends shaping web development in 2025, from AI tools to WebAssembly and edge computing.', 1, 'Technology', 10, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                   ('Building Scalable Microservices', 'Microservices architecture has become the go-to pattern for building scalable applications. Here''s what I''ve learned from building and maintaining microservices at scale.

## Service Boundaries

The most critical aspect of microservices is defining proper service boundaries. Each service should have a single responsibility and own its data.

## Communication Patterns

There are several ways services can communicate:
- REST APIs for synchronous communication
- Message queues for asynchronous processing
- Event streams for real-time data flow

## Observability is Key

With multiple services, observability becomes crucial. Implement comprehensive logging, metrics, and tracing from day one.

## Database Per Service

Each microservice should have its own database. This ensures loose coupling and allows each service to choose the best database for its needs.

## Challenges

Microservices aren''t without challenges:
- Increased complexity in deployment
- Network latency between services
- Data consistency across services
- Debugging distributed systems

Start small, learn the patterns, and scale as needed. Don''t over-engineer from the start!', 'Learn practical approaches to building scalable microservices architecture with proper service boundaries and communication patterns.', 1, 'Technology', 12, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                   ('Hidden Gems of Southeast Asia', 'Beyond the popular tourist destinations, Southeast Asia is full of hidden gems waiting to be discovered. Here are my favorite off-the-beaten-path locations.

## Kampot, Cambodia

This riverside town is known for its pepper farms and colonial architecture. The laid-back atmosphere makes it perfect for a relaxing getaway.

## Luang Prabang, Laos

A UNESCO World Heritage site that has managed to maintain its charm despite increasing tourism. Wake up early to witness the daily alms giving ceremony.

## Pai, Thailand

Nestled in the mountains of Northern Thailand, Pai offers stunning natural beauty, hot springs, and a vibrant backpacker scene.

## El Nido, Philippines

While becoming more popular, El Nido still feels like a paradise. The limestone cliffs and crystal-clear waters are absolutely breathtaking.

## Hoi An, Vietnam

This ancient town is a photographer''s dream. The lantern-lit streets at night create a magical atmosphere.

Pack your bags and explore these amazing destinations before they become too crowded!', 'Explore hidden gems of Southeast Asia including ancient towns, mountain villages, and pristine islands perfect for authentic travel experiences.', 4, 'Travel', 7, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                   ('Solo Travel: Self-Discovery', 'Solo travel changed my life. Here''s why everyone should try traveling alone at least once.

## Freedom and Flexibility

When you travel alone, you have complete freedom to change your plans on a whim. Want to stay an extra day in a city? Go for it!

## Meeting New People

Ironically, solo travel often leads to more social interactions. You''re more approachable when alone and more likely to strike up conversations.

## Building Confidence

There''s something empowering about navigating a foreign country on your own. Each challenge overcome builds confidence that extends beyond travel.

## Self-Reflection

Solo travel provides space for introspection. Without constant companionship, you have time to think, reflect, and grow.

## Practical Tips

1. Start with an easy destination
2. Stay in hostels to meet other travelers
3. Keep loved ones informed of your plans
4. Trust your instincts
5. Embrace the uncertainty

The world is waiting. Don''t let the fear of traveling alone hold you back!', 'Discover why solo travel transforms lives through freedom, self-discovery, and building confidence, with practical tips for your first adventure.', 4, 'Travel', 6, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                   ('The Art of Perfect Pasta', 'Making perfect pasta is both an art and a science. After years of experimentation, here''s what I''ve learned.

## Choose Quality Ingredients

Start with high-quality pasta. Fresh pasta has a different texture than dried, but both can be excellent when used appropriately.

## Salt Your Water Properly

The water should be "as salty as the sea." This is your only chance to season the pasta itself, so don''t be shy with the salt.

## Save That Pasta Water

The starchy pasta water is liquid gold. It helps emulsify your sauce and brings everything together.

## Al Dente is the Goal

Pasta should be cooked until it''s tender but still has a slight bite. Overcooked pasta is mushy and unpleasant.

## Finish in the Sauce

Never rinse your pasta! Transfer it directly to the sauce and finish cooking it there for a minute or two.

## Classic Recipes to Master

- Cacio e Pepe: Just cheese, pepper, and pasta water
- Carbonara: Eggs, guanciale, pecorino, and black pepper
- Aglio e Olio: Garlic, olive oil, and chili flakes

Practice these classics, and you''ll understand the fundamentals of great pasta cooking!', 'Master restaurant-quality pasta techniques from selecting ingredients to achieving perfect al dente texture and classic Italian sauces.', 5, 'Food', 9, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                   ('Street Food Around the World', 'Some of my best food experiences have been from street vendors. Here''s a culinary journey through the world''s best street food.

## Bangkok, Thailand

From pad thai to mango sticky rice, Bangkok''s street food scene is legendary. The flavors are bold, and the prices are unbeatable.

## Mexico City, Mexico

Tacos al pastor, elotes, and churros – Mexico City''s street food is a celebration of flavor. Don''t miss the markets!

## Istanbul, Turkey

Simit, döner kebab, and midye dolma (stuffed mussels) are just the beginning. Turkish street food is incredibly diverse.

## Mumbai, India

Vada pav, pav bhaji, and chaat – Mumbai''s street food is a spice lover''s paradise. The variety is overwhelming in the best way.

## Marrakech, Morocco

The Jemaa el-Fnaa square transforms into a massive food market at night. The harira soup and grilled meats are must-tries.

## Safety Tips

1. Look for busy stalls (high turnover means fresh food)
2. Watch your food being prepared
3. Drink bottled water
4. Trust your instincts

Street food is one of the best ways to experience a culture''s culinary traditions!', 'Embark on a global culinary journey exploring street food from Bangkok pad thai to Marrakech markets, with safety tips included.', 5, 'Food', 8, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                   ('Mindfulness in the Digital Age', 'In our hyperconnected world, finding moments of peace and presence has become more important than ever.

## The Problem with Always Being "On"

Constant connectivity has blurred the boundaries between work and personal life. We''re expected to respond immediately, stay updated, and always be available.

## What is Mindfulness?

Mindfulness is the practice of being present in the moment without judgment. It''s about awareness and acceptance of your current experience.

## Practical Mindfulness Techniques

### Digital Detox
Set aside time each day to disconnect from devices. Start with just 30 minutes and gradually increase.

### Mindful Morning Routine
Begin your day without immediately checking your phone. Take time to breathe, stretch, and set intentions.

### The 5-4-3-2-1 Technique
When feeling overwhelmed, notice:
- 5 things you can see
- 4 things you can touch
- 3 things you can hear
- 2 things you can smell
- 1 thing you can taste

### Mindful Breathing
Take 5 minutes to focus solely on your breath. When your mind wanders (and it will), gently bring your attention back.

## Building the Habit

Start small. Even 2 minutes of mindfulness practice daily is better than an hour once a week. Consistency matters more than duration.

Finding balance in our digital age isn''t easy, but it''s essential for our wellbeing.', 'Discover practical mindfulness techniques for the digital age, including digital detox strategies and mindful breathing exercises.', 6, 'Lifestyle', 7, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                   ('A Sustainable Morning Routine', 'Your morning sets the tone for your entire day. Here''s how to create a morning routine that''s both effective and sustainable.

## Why Morning Routines Matter

How you start your day influences your mood, productivity, and overall wellbeing. A good morning routine provides structure and sets positive momentum.

## The Elements of a Good Morning Routine

### 1. Consistent Wake Time
Try to wake up at the same time every day, even on weekends. This regulates your circadian rhythm.

### 2. Hydration
Drink a glass of water first thing. Your body has been without fluids for 7-8 hours.

### 3. Movement
Whether it''s yoga, stretching, or a full workout, get your body moving. Even 10 minutes makes a difference.

### 4. Nourishment
Eat a balanced breakfast. Skipping breakfast can lead to energy crashes later.

### 5. Mindfulness
Spend a few minutes in meditation, journaling, or simply sitting quietly with your thoughts.

### 6. Planning
Review your goals and priorities for the day. This helps you approach the day with intention.

## Making It Stick

### Start Small
Don''t try to implement everything at once. Add one element at a time.

### Be Flexible
Your routine should serve you, not stress you. Adapt it to your life circumstances.

### Prepare the Night Before
Lay out your workout clothes, prep breakfast ingredients, and organize your space.

### Track Your Progress
Keep a simple log for the first month to build the habit.

## My Routine

I wake at 6 AM, drink water, do 15 minutes of yoga, meditate for 10 minutes, eat breakfast while reading, and plan my day. It takes about 90 minutes total, and it''s transformed my life.

Remember, the best morning routine is one you''ll actually stick to!', 'Create a sustainable morning routine combining hydration, movement, mindfulness, and planning to start your day with intention and purpose.', 6, 'Lifestyle', 8, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                                                                   ('Minimalism: Less is More', 'Embracing minimalism changed my life. Here''s why owning less can mean living more.

## What is Minimalism?

Minimalism isn''t about deprivation. It''s about intentionality – keeping only what adds value to your life and letting go of the rest.

## The Benefits

### Mental Clarity
Less clutter in your physical space means less clutter in your mind. It''s easier to focus when you''re not surrounded by distractions.

### Financial Freedom
When you stop buying things you don''t need, you save money. This creates financial breathing room and reduces stress.

### More Time
Less stuff means less time cleaning, organizing, and maintaining. You get hours back each week.

### Environmental Impact
Consuming less means producing less waste and using fewer resources. It''s better for the planet.

## Getting Started

### The 90/90 Rule
If you haven''t used something in 90 days and won''t use it in the next 90, consider letting it go.

### One In, One Out
Whenever you bring something new into your home, let go of something old.

### Question Your Purchases
Before buying something, ask yourself:
- Do I need this or just want it?
- Where will I keep it?
- Will I still value this in a year?

### Start with a Category
Begin with something easy like old magazines or expired pantry items. Build momentum before tackling sentimental items.

## It''s a Journey

Minimalism looks different for everyone. I still have possessions I love and use. The difference is that everything I own serves a purpose or brings joy.

You don''t have to live in a white cube to be a minimalist. Find what works for you.', 'Explore how minimalism creates mental clarity, financial freedom, and environmental consciousness through intentional living practices.', 6, 'Lifestyle', 9, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert tags for posts
INSERT INTO post_tags (post_id, tag) VALUES
                                         (1, 'React'),
                                         (1, 'JavaScript'),
                                         (1, 'Web Development'),
                                         (1, 'Hooks'),
                                         (2, 'Web Development'),
                                         (2, 'AI'),
                                         (2, 'Future Tech'),
                                         (2, 'WebAssembly'),
                                         (3, 'Microservices'),
                                         (3, 'Architecture'),
                                         (3, 'Backend'),
                                         (3, 'Scalability'),
                                         (4, 'Travel'),
                                         (4, 'Southeast Asia'),
                                         (4, 'Adventure'),
                                         (4, 'Backpacking'),
                                         (5, 'Travel'),
                                         (5, 'Adventure'),
                                         (5, 'Personal Growth'),
                                         (5, 'Tips'),
                                         (6, 'Cooking'),
                                         (6, 'Italian Cuisine'),
                                         (6, 'Pasta'),
                                         (6, 'Recipes'),
                                         (7, 'Street Food'),
                                         (7, 'Travel'),
                                         (7, 'Food Culture'),
                                         (7, 'Global Cuisine'),
                                         (8, 'Mindfulness'),
                                         (8, 'Wellness'),
                                         (8, 'Mental Health'),
                                         (8, 'Digital Detox'),
                                         (9, 'Productivity'),
                                         (9, 'Wellness'),
                                         (9, 'Morning Routine'),
                                         (9, 'Habits'),
                                         (10, 'Minimalism'),
                                         (10, 'Decluttering'),
                                         (10, 'Intentional Living'),
                                         (10, 'Sustainability');


-- Spread creation times so "newest first" has a stable order (all rows used to share one timestamp)
UPDATE posts SET created_at = now() - ((11 - id) * interval '1 day'),
    updated_at = created_at;